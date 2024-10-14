package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.friends.Friendship;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.management.AskAddFriendNotify;

public class PacketAskAddFriendNotify extends BaseTypedPacket<AskAddFriendNotify> {

	public PacketAskAddFriendNotify(Friendship friendship) {
        super(new AskAddFriendNotify());
        proto.setTargetUid(friendship.getFriendId());
        proto.setTargetFriendBrief(friendship.toProto());
	}
}
