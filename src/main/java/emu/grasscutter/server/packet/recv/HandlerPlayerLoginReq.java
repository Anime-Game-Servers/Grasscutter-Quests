package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.game.GameSession.SessionState;
import emu.grasscutter.server.packet.send.PacketPlayerLoginRsp;
import org.anime_game_servers.multi_proto.gi.messages.player.PlayerLoginReq;

 // Sends initial data packets
public class HandlerPlayerLoginReq extends TypedPacketHandler<PlayerLoginReq> {

    @Override
    public void handle(GameSession session, byte[] header, PlayerLoginReq req) throws Exception {
        // Check
        if (session.getAccountId() == null) {
            session.close();
            return;
        }

        // Authenticate session
        if (!req.getToken().equals(session.getSessionToken())) {
            session.close();
            return;
        }

        // Load character from db
        Player player = session.getPlayer();

        // Show opening cutscene if player has no avatars
        if (player.getAvatars().getAvatarCount() == 0) {
            // Pick character
            session.setState(SessionState.PICKING_CHARACTER);
            session.send(new BasePacket(session.getPackageIdProvider().getPacketId("DoSetPlayerBornDataNotify")));
        } else {
            // Login done
            session.getPlayer().onLogin();
        }

        // Final packet to tell client logging in is done
        session.send(new PacketPlayerLoginRsp(session));
    }

}
