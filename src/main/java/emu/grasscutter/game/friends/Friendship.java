package emu.grasscutter.game.friends;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.Transient;
import emu.grasscutter.database.DatabaseHelper;
import emu.grasscutter.game.player.Player;
import lombok.Getter;
import lombok.Setter;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.FriendBrief;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.FriendOnlineState;
import org.anime_game_servers.multi_proto.gi.messages.general.PlatformType;
import org.anime_game_servers.multi_proto.gi.messages.general.ProfilePicture;
import org.bson.types.ObjectId;

@Entity(value = "friendships", useDiscriminator = false)
public class Friendship {
    @Id private ObjectId id;
    @Getter @Setter @Transient private Player owner;
    @Getter @Setter @Indexed private int ownerId;
    @Getter @Setter @Indexed private int friendId;
    @Getter @Setter private boolean isFriend;
    @Getter @Setter private int askerId;
    @Getter private PlayerProfile friendProfile;

    @Deprecated // Morphia use only
    public Friendship() {
    }

    public Friendship(Player owner, Player friend, Player asker) {
        this.setOwner(owner);
        this.ownerId = owner.getUid();
        this.friendId = friend.getUid();
        this.friendProfile = friend.getProfile();
        this.askerId = asker.getUid();
    }

    public void setFriendProfile(Player character) {
        if (character == null || this.friendId != character.getUid()) return;
        this.friendProfile = character.getProfile();
    }

    public boolean isOnline() {
        return getFriendProfile().getPlayer() != null;
    }

    public void save() {
        DatabaseHelper.saveFriendship(this);
    }

    public void delete() {
        DatabaseHelper.deleteFriendship(this);
    }

    public FriendBrief toProto() {
        FriendBrief proto = new FriendBrief();
        proto.setUid(getFriendProfile().getUid());
        proto.setNickname(getFriendProfile().getName());
        proto.setLevel(getFriendProfile().getPlayerLevel());
        proto.setProfilePicture(new ProfilePicture(getFriendProfile().getAvatarId()));
        proto.setWorldLevel(getFriendProfile().getWorldLevel());
        proto.setSignature(getFriendProfile().getSignature());
        proto.setOnlineState(getFriendProfile().isOnline() ? FriendOnlineState.FRIEND_ONLINE : FriendOnlineState.FRIEND_DISCONNECT);
        proto.setMpModeAvailable(true);
        proto.setLastActiveTime(getFriendProfile().getLastActiveTime());
        proto.setNameCardId(getFriendProfile().getNameCard());
        proto.setParam(getFriendProfile().getDaysSinceLogin());
        proto.setGameSource(true);
        proto.setPlatformType(PlatformType.PC);
        return proto;
    }
}
