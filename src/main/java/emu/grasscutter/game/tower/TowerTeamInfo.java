package emu.grasscutter.game.tower;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Transient;
import emu.grasscutter.game.avatar.TowerAvatar;
import emu.grasscutter.game.player.Player;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.TowerTeam;
import lombok.Builder;
import lombok.Getter;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity @Getter @Builder(builderMethodName = "of")
public class TowerTeamInfo {
    private int teamId;
    private final List<TowerAvatar> avatars = new ArrayList<>();
    @Transient private List<TowerAvatar> tempAvatars;

    public static TowerTeamInfo create(TowerTeam newTeam, Player player) {
        val info = TowerTeamInfo.of().teamId(newTeam.getTowerTeamId()).build();
        info.getAvatars().addAll(newTeam.getAvatarGuidList().stream()
            .map(player.getAvatars()::getAvatarByGuid).filter(Objects::nonNull).map(TowerAvatar::new).toList());
        return info;
    }

    public void copy() {
        if (this.tempAvatars == null) {
            this.tempAvatars = new ArrayList<>();
        }
        this.tempAvatars.clear();
        this.tempAvatars.addAll(this.avatars.stream().map(TowerAvatar::new).toList());
    }

    public void update() {
        this.avatars.clear();
        this.avatars.addAll(this.tempAvatars.stream().map(TowerAvatar::new).toList());
    }

    public TowerTeam toProto() {
        val proto = new TowerTeam();
        proto.setTowerTeamId(this.teamId);
        proto.setAvatarGuidList(Optional.ofNullable(this.tempAvatars)
            .orElse(this.avatars).stream()
            .map(TowerAvatar::getGuid).toList());
        return proto;
    }
}
