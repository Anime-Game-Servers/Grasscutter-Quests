package emu.grasscutter.server.packet.send;

import emu.grasscutter.GameConstants;
import emu.grasscutter.game.friends.Friendship;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.FriendOnlineState;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.FriendBrief;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.GetPlayerFriendListRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.PlatformType;
import org.anime_game_servers.multi_proto.gi.messages.general.ProfilePicture;

import java.util.ArrayList;
import java.util.List;

import static emu.grasscutter.config.Configuration.GAME_INFO;

public class PacketGetPlayerFriendListRsp extends BaseTypedPacket<GetPlayerFriendListRsp> {

    public PacketGetPlayerFriendListRsp(Player player) {
        super(new GetPlayerFriendListRsp());
        var serverAccount = GAME_INFO.serverAccount;
        FriendBrief serverFriend = new FriendBrief();
        serverFriend.setUid(GameConstants.SERVER_CONSOLE_UID);
        serverFriend.setNickname(serverAccount.nickName);
        serverFriend.setLevel(serverAccount.adventureRank);
        serverFriend.setProfilePicture(new ProfilePicture(serverAccount.avatarId));
        serverFriend.setWorldLevel(serverAccount.worldLevel);
        serverFriend.setSignature(serverAccount.signature);
        serverFriend.setLastActiveTime((int) (System.currentTimeMillis() / 1000f));
        serverFriend.setNameCardId(serverAccount.nameCardId);
        serverFriend.setOnlineState(FriendOnlineState.FRIEND_ONLINE);
        serverFriend.setParam(1);
        serverFriend.setGameSource(true);
        serverFriend.setPlatformType(PlatformType.PC);

        List<FriendBrief> friendList = new ArrayList<>();
        friendList.add(serverFriend);
        friendList.addAll(player.getFriendsList().getFriends().values().stream().map(Friendship::toProto).toList());
        proto.setFriendList(friendList);
    }
}
