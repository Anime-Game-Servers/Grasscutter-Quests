package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.scene.map.MarkMapReq;

public class HandlerMarkMapReq extends TypedPacketHandler<MarkMapReq> {

	@Override
    public void handle(GameSession session, byte[] header, MarkMapReq req) throws Exception {
		session.getPlayer().getMapMarksManager().handleMapMarkReq(req);
	}
}
