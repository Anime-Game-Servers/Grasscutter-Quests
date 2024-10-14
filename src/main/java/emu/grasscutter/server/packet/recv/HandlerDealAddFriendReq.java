package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.management.DealAddFriendReq;

public class HandlerDealAddFriendReq extends TypedPacketHandler<DealAddFriendReq> {
	@Override
    public void handle(GameSession session, byte[] header, DealAddFriendReq req) throws Exception {
		session.getPlayer().getFriendsList().handleFriendRequest(req.getTargetUid(), req.getDealAddFriendResult());
	}

}
