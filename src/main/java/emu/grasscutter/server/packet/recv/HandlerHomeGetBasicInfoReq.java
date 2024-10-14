package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketHomeBasicInfoNotify;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.base_info.HomeGetBasicInfoReq;

public class HandlerHomeGetBasicInfoReq extends TypedPacketHandler<HomeGetBasicInfoReq> {
	@Override
    public void handle(GameSession session, byte[] header, HomeGetBasicInfoReq req) throws Exception {
		session.send(new PacketHomeBasicInfoNotify(session.getPlayer(), false));
	}
}
