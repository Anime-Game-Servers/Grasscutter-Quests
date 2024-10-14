package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketChangeGameTimeRsp;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.player.game_time.ChangeGameTimeReq;

public class HandlerChangeGameTimeReq extends TypedPacketHandler<ChangeGameTimeReq> {

    @Override
    public void handle(GameSession session, byte[] header, ChangeGameTimeReq req) throws Exception {
        val result = session.getPlayer().getWorld().changeTime(req.getGameTime(), req.getExtraDays(), req.isForceSet());
        session.getPlayer().sendPacket(new PacketChangeGameTimeRsp(session.getPlayer(), req.getExtraDays(), result));
    }
}
