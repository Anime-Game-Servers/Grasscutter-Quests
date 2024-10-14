package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.management.DeleteFriendRsp;

public class PacketDeleteFriendRsp extends BaseTypedPacket<DeleteFriendRsp> {
	public PacketDeleteFriendRsp(int targetUid) {
        super(new DeleteFriendRsp());
        proto.setTargetUid(targetUid);
	}
}
