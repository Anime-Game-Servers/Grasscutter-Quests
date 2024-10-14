package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetShopmallDataRsp;
import org.anime_game_servers.multi_proto.gi.messages.shop.GetShopmallDataReq;

public class HandlerGetShopmallDataReq extends TypedPacketHandler<GetShopmallDataReq> {

	@Override
	public void handle(GameSession session, byte[] header, GetShopmallDataReq rew) throws Exception {
		// TODO add the correct shops
		session.send(new PacketGetShopmallDataRsp());
	}

}
