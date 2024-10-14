package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetPlayerFriendListRsp;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.GetPlayerFriendListReq;

public class HandlerGetPlayerFriendListReq extends TypedPacketHandler<GetPlayerFriendListReq> {
	@Override
    public void handle(GameSession session, byte[] header, GetPlayerFriendListReq req) throws Exception {
		//session.send(new PacketGetPlayerAskFriendListRsp(session.getPlayer()));
		session.send(new PacketGetPlayerFriendListRsp(session.getPlayer()));
	}
}
