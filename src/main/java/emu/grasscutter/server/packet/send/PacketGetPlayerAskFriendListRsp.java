package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.friends.Friendship;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.GetPlayerAskFriendListRsp;

public class PacketGetPlayerAskFriendListRsp extends BaseTypedPacket<GetPlayerAskFriendListRsp> {

	public PacketGetPlayerAskFriendListRsp(Player player) {
        super(new GetPlayerAskFriendListRsp());

        proto.setAskFriendList(player.getFriendsList().getPendingFriends().values().stream()
            .filter(friendship -> friendship.getAskerId() != player.getUid())
            .map(Friendship::toProto)
            .toList());
	}
}
