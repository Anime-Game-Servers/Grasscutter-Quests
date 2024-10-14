package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketPlayerGetForceQuitBanInfoRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.player.PlayerGetForceQuitBanInfoReq;

public class HandlerPlayerGetForceQuitBanInfoReq extends TypedPacketHandler<PlayerGetForceQuitBanInfoReq> {
    @Override
    public void handle(GameSession session, byte[] header, PlayerGetForceQuitBanInfoReq req) throws Exception {
        if (session.getServer().getMultiplayerSystem().leaveCoop(session.getPlayer())) {
            // Success
            session.send(new PacketPlayerGetForceQuitBanInfoRsp(Retcode.RET_SUCC));
        } else {
            // Fail
            session.send(new PacketPlayerGetForceQuitBanInfoRsp(Retcode.RET_SVR_ERROR));
        }
    }
}
