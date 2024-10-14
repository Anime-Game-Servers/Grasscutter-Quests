package emu.grasscutter.game.tower;

import dev.morphia.annotations.Entity;
import lombok.*;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.TowerMonthlyBrief;

@Entity
@Getter
@Builder(builderMethodName = "of")
@AllArgsConstructor
@NoArgsConstructor
public class TowerMonthlyBriefInfo {
    private int towerScheduleId;
    private int bestFloorIndex;
    private int bestLevelIndex;
    private int totalStartCount;

    public static TowerMonthlyBriefInfo create(int scheduleId) {
        return TowerMonthlyBriefInfo.of()
            .towerScheduleId(scheduleId)
            .build();
    }

    public void update(int scheduleId, int bestFloorIndex, int bestLevelIndex, int totalStartCount) {
        this.towerScheduleId = scheduleId;
        this.bestFloorIndex = bestFloorIndex;
        this.bestLevelIndex = bestLevelIndex;
        this.totalStartCount = totalStartCount;
    }

    public TowerMonthlyBrief toProto() {
        val proto = new TowerMonthlyBrief();
        proto.setTowerScheduleId(this.towerScheduleId);
        proto.setBestFloorIndex(this.bestFloorIndex);
        proto.setBestLevelIndex(this.bestLevelIndex);
        proto.setTotalStarCount(this.totalStartCount);
        return proto;
    }
}
