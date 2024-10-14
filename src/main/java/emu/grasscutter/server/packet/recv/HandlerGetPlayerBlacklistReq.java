package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.server.game.GameSession;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.community.blocking.GetPlayerBlacklistReq;
import org.anime_game_servers.multi_proto.gi.messages.community.blocking.GetPlayerBlacklistRsp;

public class HandlerGetPlayerBlacklistReq extends TypedPacketHandler<GetPlayerBlacklistReq> {

	@Override
	public void handle(GameSession session, byte[] header, GetPlayerBlacklistReq payload) throws Exception {
        val rsp = new BaseTypedPacket<>(new GetPlayerBlacklistRsp()){};
		session.send(rsp.buildHeader(3));
	}

}
