package emu.grasscutter.game.dungeons.dungeon_entry;

import dev.morphia.annotations.Entity;
import emu.grasscutter.GameConstants;
import emu.grasscutter.data.excels.DungeonSerialData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.WeeklyBossResinDiscountInfo;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

@Entity
@Builder(builderMethodName = "of")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyBossRecord {
    private int serialId;
    private int discountNum;
    private int discountNumLimit;
    private int resinCost;
    private int originalResinCost;
    private String lastCycledTime;
    private int takeNum;
    private int maxTakeNumLimit;
    private RosterCycleRecord rosterCycleRecord;

    public static WeeklyBossRecord create(DungeonSerialData serialData) {
        return WeeklyBossRecord.of()
            .serialId(serialData.getId())
            .discountNum(0)
            .discountNumLimit(GameConstants.WEEKLY_BOSS_RESIN_DISCOUNT_COUNT)
            .originalResinCost(serialData.getTakeCost())
            .resinCost((int) (serialData.getTakeCost() * GameConstants.WEEKLY_BOSS_RESIN_DISCOUNT_VALUE))
            .takeNum(0)
            .maxTakeNumLimit(serialData.getMaxTakeNum())
            .lastCycledTime(DungeonEntryItem.getLastRefreshTimeStr())
            .build();
    }

    public void buildRosterRecord(int rosterId) {
        this.rosterCycleRecord = RosterCycleRecord.create(rosterId);
    }

    public int getNextRefreshTime(){
        if (this.lastCycledTime == null || this.lastCycledTime.isBlank()) return 0;
        // Get the current date and time in the specified time zone
        ZonedDateTime now = ZonedDateTime.now(GameConstants.ZONE_ID);
        ZonedDateTime nextMonday = now.with(TemporalAdjusters.next(GameConstants.WEEKLY_BOSS_RESIN_DISCOUNT_REFRESH_DAY))
            .with(LocalTime.of(GameConstants.REFRESH_HOUR, 0, 0));
        return (int) Instant.now().plus(Duration.between(now, nextMonday)).getEpochSecond();
    }

    void update() {
        if (canTakeReward()) this.takeNum += 1;
    }

    void sync() {
        if(this.discountNum < this.discountNumLimit) this.discountNum += 1;
    }

    public void reset() {
        this.discountNum = 0;
        this.takeNum = 0;
        this.lastCycledTime = DungeonEntryItem.getLastRefreshTimeStr();
        Optional.ofNullable(this.rosterCycleRecord).ifPresent(RosterCycleRecord::reset);
    }

    private int getResinCost() {
        return this.discountNum < this.discountNumLimit ? this.resinCost : this.originalResinCost;
    }

    public boolean canTakeReward() {
        return this.takeNum < this.maxTakeNumLimit;
    }

    public WeeklyBossResinDiscountInfo toProto() {
        return new WeeklyBossResinDiscountInfo(this.discountNum, this.discountNumLimit,
            getResinCost(), this.originalResinCost);
    }
}
