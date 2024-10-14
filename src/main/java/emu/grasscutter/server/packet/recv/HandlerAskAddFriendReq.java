package emu.grasscutter.server.packet.recv;

import org.anime_game_servers.multi_proto.gi.messages.community.friends.management.AskAddFriendReq;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;

public class HandlerAskAddFriendReq extends TypedPacketHandler<AskAddFriendReq> {

	@Override
    public void handle(GameSession session, byte[] header, AskAddFriendReq req) throws Exception {
		session.getPlayer().getFriendsList().sendFriendRequest(req.getTargetUid());
	}

}
