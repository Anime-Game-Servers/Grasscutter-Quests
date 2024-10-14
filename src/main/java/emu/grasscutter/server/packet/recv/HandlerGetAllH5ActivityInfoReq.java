package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketH5ActivityIdsNotify;
import org.anime_game_servers.multi_proto.gi.messages.web_event.GetAllH5ActivityInfoReq;

public class HandlerGetAllH5ActivityInfoReq extends TypedPacketHandler<GetAllH5ActivityInfoReq> {
	@Override
    public void handle(GameSession session, byte[] header, GetAllH5ActivityInfoReq req) throws Exception {
		session.send(new PacketH5ActivityIdsNotify());
	}
}
