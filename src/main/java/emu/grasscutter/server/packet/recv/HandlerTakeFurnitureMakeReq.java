package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.furniture.TakeFurnitureMakeReq;

public class HandlerTakeFurnitureMakeReq extends TypedPacketHandler<TakeFurnitureMakeReq> {
	@Override
    public void handle(GameSession session, byte[] header, TakeFurnitureMakeReq req) throws Exception {
        session.getPlayer().getFurnitureManager().take(req.getIndex(), req.getMakeId(), req.isFastFinish());
	}
}
