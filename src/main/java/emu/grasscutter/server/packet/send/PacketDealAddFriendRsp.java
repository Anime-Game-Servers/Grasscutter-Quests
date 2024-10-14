package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.management.DealAddFriendResultType;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.management.DealAddFriendRsp;

public class PacketDealAddFriendRsp extends BaseTypedPacket<DealAddFriendRsp> {

	public PacketDealAddFriendRsp(int targetUid, DealAddFriendResultType result) {
        super(new DealAddFriendRsp());
        proto.setTargetUid(targetUid);
        proto.setDealAddFriendResult(result);
	}
}
