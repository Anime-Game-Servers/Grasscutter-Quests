package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.management.DeleteFriendNotify;

public class PacketDeleteFriendNotify extends BaseTypedPacket<DeleteFriendNotify> {
	public PacketDeleteFriendNotify(int targetUid) {
        super(new DeleteFriendNotify());
        proto.setTargetUid(targetUid);
	}
}
