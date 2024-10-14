package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.management.DeleteFriendReq;

public class HandlerDeleteFriendReq extends TypedPacketHandler<DeleteFriendReq> {
	@Override
    public void handle(GameSession session, byte[] header, DeleteFriendReq req) throws Exception {
		session.getPlayer().getFriendsList().deleteFriend(req.getTargetUid());
	}
}
