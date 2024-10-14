package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSkipPlayerGameTimeRsp;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.player.game_time.SkipPlayerGameTimeReq;

public class HandlerSkipPlayerGameTimeReq extends TypedPacketHandler<SkipPlayerGameTimeReq> {

    @Override
    public void handle(GameSession session, byte[] header, SkipPlayerGameTimeReq req) throws Exception {
        val result = session.getPlayer().getWorld().changeTime(req.getGameTime(), req.isForceSet());
        val gameTime = session.getPlayer().getWorld().getGameTime();
        session.getPlayer().sendPacket(new PacketSkipPlayerGameTimeRsp(session.getPlayer(), gameTime, req.getClientGameTime(), result));
    }
}
