package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetAllUnlockNameCardRsp;
import org.anime_game_servers.multi_proto.gi.messages.community.player_presentation.GetAllUnlockNameCardReq;

public class HandlerGetAllUnlockNameCardReq extends TypedPacketHandler<GetAllUnlockNameCardReq> {
	@Override
    public void handle(GameSession session, byte[] header, GetAllUnlockNameCardReq req) throws Exception {
		session.send(new PacketGetAllUnlockNameCardRsp(session.getPlayer()));
	}

}
