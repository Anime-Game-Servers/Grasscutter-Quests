package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.management.AskAddFriendRsp;

public class PacketAskAddFriendRsp extends BaseTypedPacket<AskAddFriendRsp> {

	public PacketAskAddFriendRsp(int targetUid) {
        super(new AskAddFriendRsp());
        proto.setTargetUid(targetUid);
	}
}
