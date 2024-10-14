package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.furniture.FurnitureMakeReq;

public class HandlerFurnitureMakeReq extends TypedPacketHandler<FurnitureMakeReq> {
	@Override
    public void handle(GameSession session, byte[] header, FurnitureMakeReq req) throws Exception {
		session.getPlayer().getFurnitureManager().queryStatus();
	}

}
