package emu.grasscutter.game.dungeons.dungeon_entry;

import dev.morphia.annotations.Entity;
import emu.grasscutter.data.GameData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Entity
@Builder(builderMethodName = "of")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RosterCycleRecord {
    private int rosterId;
    private int selectedPool;
    private String lastCycledTime;

    public static RosterCycleRecord create(int rosterId) {
        return RosterCycleRecord.of()
            .rosterId(rosterId)
            .lastCycledTime(DungeonEntryItem.getLastRefreshTimeStr())
            .selectedPool(0)
            .build();
    }

    public void reset() {
        Optional.ofNullable(GameData.getDungeonRosterDataMap().get(this.rosterId))
            .ifPresent(data -> this.selectedPool = data.getNextPool(this.selectedPool));
        this.lastCycledTime = DungeonEntryItem.getLastRefreshTimeStr();
    }
}
