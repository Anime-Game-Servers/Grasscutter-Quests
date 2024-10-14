package emu.grasscutter.server.packet.recv;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.database.DatabaseHelper;
import emu.grasscutter.game.Account;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.event.game.PlayerCreationEvent;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.game.GameSession.SessionState;
import emu.grasscutter.server.packet.send.PacketGetPlayerTokenRsp;
import emu.grasscutter.utils.ByteHelper;
import emu.grasscutter.utils.Crypto;
import emu.grasscutter.utils.Utils;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.player.GetPlayerTokenReq;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import javax.crypto.Cipher;

import static emu.grasscutter.config.Configuration.ACCOUNT;
import static emu.grasscutter.config.Configuration.GAME_OPTIONS;

import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.security.Signature;

public class HandlerGetPlayerTokenReq extends TypedPacketHandler<GetPlayerTokenReq> {

    public static class VerifyResult {
        public int retcode;
        public VerifyResultData data;
    }
    public static class VerifyResultIpInfo {
        public String country_code;
    }
    public static class VerifyResultData {
        public boolean guest;
        public int account_type;
        public int account_uid;
        public VerifyResultIpInfo ip_info;
    }
    @Override
    public void handle(GameSession session, byte[] header, GetPlayerTokenReq req) throws Exception {

        // Authenticate
        String accountId = null;
        var reservedUid = 0;
        var isAccountBanned = false;
        val sdkServer= Grasscutter.config.account.sdkServer;
        if(sdkServer != null && !sdkServer.isBlank()){
            // TODO - implement verify of combo toke from sdk server
            SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
            HttpClient httpclient = new HttpClient(sslContextFactory);
            httpclient.start();
            val request = httpclient.POST(sdkServer);
            request.content(new StringContentProvider("{\"combo_token\": \""+req.getAccountToken()+"\", \"open_id\": \""+req.getAccountUid()+"\"}"));

            val response = request.send();
            val body = response.getContentAsString();
            val gson = new GsonBuilder().create();
            val comboTokenResJson = gson.fromJson(body, VerifyResult.class);
            if(comboTokenResJson.retcode != 0){
                session.close();
                return;
            }
            accountId = Integer.toString(comboTokenResJson.data.account_uid);
        } else {
            Account account = DatabaseHelper.getAccountById(req.getAccountUid());
            // Set account
            session.setAccount(account);
            if (account == null || !account.getToken().equals(req.getAccountToken())) {
                session.close();
                return;
            }
            accountId = account.getId();
            isAccountBanned = account.isBanned();
            reservedUid = account.getReservedPlayerUid();
        }

        if(accountId == null || !accountId.equals(req.getAccountUid())){
            return;
        }
        session.setAccountId(accountId);
        session.setSessionToken(req.getAccountToken());

        // Check if player object exists in server
        // NOTE: CHECKING MUST SITUATED HERE (BEFORE getPlayerByUid)! because to save firstly ,to load secondly !!!
        // TODO - optimize
        boolean kicked = false;
        Player exists = Grasscutter.getGameServer().getPlayerByAccountId(accountId);
        if (exists != null) {
            GameSession existsSession = exists.getSession();
            if (existsSession != session) {// No self-kicking
                exists.onLogout();//must save immediately , or the below will load old data
                existsSession.close();
                Grasscutter.getLogger().warn("Player {} with id {} was kicked due to duplicated login", exists.getNickname(), accountId);
                kicked = true;
            }
        }

        //NOTE: If there are 5 online players, max count of player is 5,
        // a new client want to login by kicking one of them ,
        // I think it should be allowed
        if (!kicked) {
            // Max players limit
            if (ACCOUNT.maxPlayer > -1 && Grasscutter.getGameServer().getPlayers().size() >= ACCOUNT.maxPlayer) {
                session.close();
                return;
            }
        }

        // Call creation event.
        PlayerCreationEvent event = new PlayerCreationEvent(session, Player.class);
        event.call();

        // Get player.
        Player player = DatabaseHelper.getPlayerByAccount(accountId, event.getPlayerClass());

        if (player == null) {
            int nextPlayerUid = DatabaseHelper.getNextPlayerId(reservedUid);

            // Create player instance from event.
            player = event.getPlayerClass().getDeclaredConstructor(GameSession.class).newInstance(session);

            // Save to db
            DatabaseHelper.generatePlayerUid(player, nextPlayerUid);
        }

        // Set player object for session
        session.setPlayer(player);

        // Checks if the player is banned
        if (isAccountBanned) {
            session.setState(SessionState.ACCOUNT_BANNED);
            session.send(new PacketGetPlayerTokenRsp(session, Retcode.RET_BLACK_UID, "FORBID_CHEATING_PLUGINS", session.getAccount().getBanEndTime()));
            return;
        }

        // Load player from database
        player.loadFromDatabase();

        // Set session state
        session.setUseSecretKey(true);
        session.setState(SessionState.WAITING_FOR_LOGIN);

        // Only >= 2.7.50 has this
        if (req.getKeyId() > 0) {
            try {
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.DECRYPT_MODE, Crypto.CUR_SIGNING_KEY);

                var client_seed_encrypted = Utils.base64Decode(req.getClientRandKey());
                var client_seed = ByteBuffer.wrap(cipher.doFinal(client_seed_encrypted))
                    .getLong();

                byte[] seed_bytes = ByteBuffer.wrap(new byte[8])
                    .putLong(Crypto.ENCRYPT_SEED ^ client_seed)
                    .array();

                cipher.init(Cipher.ENCRYPT_MODE, Crypto.EncryptionKeys.get(req.getKeyId()));
                var seed_encrypted = cipher.doFinal(seed_bytes);

                Signature privateSignature = Signature.getInstance("SHA256withRSA");
                privateSignature.initSign(Crypto.CUR_SIGNING_KEY);
                privateSignature.update(seed_bytes);

                session.send(new PacketGetPlayerTokenRsp(session, Utils.base64Encode(seed_encrypted), Utils.base64Encode(privateSignature.sign())));
            } catch (Exception ignore) {
                // Only UA Patch users will have exception
                byte[] clientBytes = Utils.base64Decode(req.getClientRandKey());
                byte[] seed = ByteHelper.longToBytes(Crypto.ENCRYPT_SEED);
                Crypto.xor(clientBytes, seed);

                String base64str = Utils.base64Encode(clientBytes);

                session.send(new PacketGetPlayerTokenRsp(session, base64str, "bm90aGluZyBoZXJl"));
            }
        } else {
            // Send packet
            session.send(new PacketGetPlayerTokenRsp(session));
        }
    }
}
