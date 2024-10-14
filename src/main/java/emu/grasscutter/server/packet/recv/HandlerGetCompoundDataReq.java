package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.GetCompoundDataReq;

public class HandlerGetCompoundDataReq extends TypedPacketHandler<GetCompoundDataReq> {
    @Override
    public void handle(GameSession session, byte[] header, GetCompoundDataReq req) throws Exception {
        session.getPlayer().getCookingCompoundManager().handleGetCompoundDataReq(req);
    }
}
