package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetShopRsp;
import org.anime_game_servers.multi_proto.gi.messages.shop.GetShopReq;

public class HandlerGetShopReq extends TypedPacketHandler<GetShopReq> {
	@Override
	public void handle(GameSession session, byte[] header, GetShopReq req) throws Exception {
		session.send(new PacketGetShopRsp(session.getPlayer(), req.getShopType()));
	}
}
