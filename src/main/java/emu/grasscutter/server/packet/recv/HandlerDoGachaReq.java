package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import org.anime_game_servers.multi_proto.gi.messages.wishing.DoGachaReq;
import emu.grasscutter.server.game.GameSession;

public class HandlerDoGachaReq extends TypedPacketHandler<DoGachaReq> {
    @Override
    public void handle(GameSession session, byte[] header, DoGachaReq req) throws Exception {
        session.getServer().getGachaSystem().doPulls(session.getPlayer(), req.getGachaScheduleId(), req.getGachaTimes());
    }
}
