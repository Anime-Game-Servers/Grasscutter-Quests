package emu.grasscutter.game.tower;

import dev.morphia.annotations.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.rotation.TowerLevelRecord;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;

@Entity
@Getter
@Builder(builderMethodName = "of")
public class TowerLevelRecordInfo {
    private int levelId;
    private final Set<Integer> satisfiedCondList = new TreeSet<>();
    @Setter
    private boolean receivedFirstPassReward;

    public static TowerLevelRecordInfo create(int levelId) {
        return TowerLevelRecordInfo.of()
            .levelId(levelId)
            .receivedFirstPassReward(false)
            .build();
    }

    public void update(int star) {
        IntStream.rangeClosed(1, star).forEach(this.satisfiedCondList::add);
    }

    public TowerLevelRecord toProto() {
        val proto = new TowerLevelRecord();
        proto.setLevelId(this.levelId);
        proto.setSatisfiedCondList(this.satisfiedCondList.stream().toList());
        return proto;
    }
}
