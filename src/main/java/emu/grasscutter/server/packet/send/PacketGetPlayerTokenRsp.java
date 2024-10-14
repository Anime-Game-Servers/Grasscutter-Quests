package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.utils.Crypto;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.player.GetPlayerTokenRsp;

public class PacketGetPlayerTokenRsp extends BaseTypedPacket<GetPlayerTokenRsp> {

    @Override
    public boolean shouldBuildHeader() {
        return true;
    }

    public PacketGetPlayerTokenRsp(GameSession session) {
        super(new GetPlayerTokenRsp());

        this.setUseDispatchKey(true);

        proto.setUid(session.getPlayer().getUid());
        proto.setToken(session.getSessionToken());
        proto.setAccountType(1);
        proto.setProficientPlayer(session.getPlayer().getAvatars().getAvatarCount() > 0); // Not sure where this goes
        proto.setSecretKeySeed(Crypto.ENCRYPT_SEED);
        proto.setSecurityCmdBuffer(Crypto.ENCRYPT_SEED_BUFFER);
        proto.setPlatformType(3);
        proto.setChannelId(1);
        proto.setCountryCode("US");
        proto.setClientVersionRandomKey("c25-314dd05b0b5f");
        proto.setRegPlatform(3);
        proto.setClientIpStr(session.getAddress().getAddress().getHostAddress());
    }

    public PacketGetPlayerTokenRsp(GameSession session, Retcode retcode, String msg, int blackEndTime) {
        super(new GetPlayerTokenRsp());

        this.setUseDispatchKey(true);

        proto.setUid(session.getPlayer().getUid());
        proto.setProficientPlayer(session.getPlayer().getAvatars().getAvatarCount() > 0);
        proto.setRetCode(retcode);
        proto.setMsg(msg);
        proto.setBlackUidEndTime(blackEndTime);
        proto.setRegPlatform(3);
        proto.setCountryCode("US");
        proto.setClientIpStr(session.getAddress().getAddress().getHostAddress());
    }

    public PacketGetPlayerTokenRsp(GameSession session, String encryptedSeed, String encryptedSeedSign) {
        super(new GetPlayerTokenRsp());

        this.setUseDispatchKey(true);

        proto.setUid(session.getPlayer().getUid());
        proto.setToken(session.getSessionToken());
        proto.setAccountType(1);
        proto.setProficientPlayer(session.getPlayer().getAvatars().getAvatarCount() > 0); // Not sure where this goes
        proto.setSecretKeySeed(Crypto.ENCRYPT_SEED);
        proto.setSecurityCmdBuffer(Crypto.ENCRYPT_SEED_BUFFER);
        proto.setPlatformType(3);
        proto.setChannelId(1);
        proto.setCountryCode("US");
        proto.setClientVersionRandomKey("c25-314dd05b0b5f");
        proto.setRegPlatform(3);
        proto.setClientIpStr(session.getAddress().getAddress().getHostAddress());
        proto.setServerRandKey(encryptedSeed);
        proto.setSign(encryptedSeedSign);
    }
}
