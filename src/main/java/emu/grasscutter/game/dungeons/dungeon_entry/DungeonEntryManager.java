package emu.grasscutter.game.dungeons.dungeon_entry;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.ScenePointEntry;
import emu.grasscutter.data.common.PointData;
import emu.grasscutter.data.excels.DungeonData;
import emu.grasscutter.data.excels.DungeonRosterData;
import emu.grasscutter.data.excels.DungeonSerialData;
import emu.grasscutter.game.player.BasePlayerManager;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.GameMainQuest;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.enums.QuestContent;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.NonNull;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.entry.DungeonEntryInfo;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.WeeklyBossResinDiscountInfo;
import org.anime_game_servers.core.gi.enums.QuestState;

import java.util.*;
import java.util.stream.Stream;

/**
 * TODO sync resin usage with statue drops
 * */
public class DungeonEntryManager extends BasePlayerManager {
    /**
     * entry: [(sceneId << 16) + pointId, dungeonId]
     * */
    private final Map<Integer, Set<Integer>> plotDungeonEntries = new Int2ObjectOpenHashMap<>();

    public DungeonEntryManager(@NonNull Player player) {
        super(player);
    }

    private DungeonEntryItem getDungeonEntryItem() {
        return this.player.getDungeonEntryItem();
    }

    private  Map<Integer, WeeklyBossRecord> getBossRecordMap() {
        return Optional.ofNullable(getDungeonEntryItem()).map(DungeonEntryItem::getBossRecordMap).orElse(Map.of());
    }

    public void onLogin() {
        // check if player needs quest dungeon entry
        this.player.getQuestManager().getMainQuests().values().stream()
            .map(GameMainQuest::getChildQuests).map(Map::values).flatMap(Collection::stream)
            .filter(quest -> quest.getState() == QuestState.QUEST_STATE_UNFINISHED)
            .forEach(this::checkQuestForDungeonEntryUpdate);

        // builds weekly boss information if its missing
        getDungeonEntryItem().checkForNewBoss();
        getDungeonEntryItem().buildRosterDungeon();
        getDungeonEntryItem().resetWeeklyBoss();
    }

    public void checkQuestForDungeonEntryUpdate(GameQuest quest){
        quest.getQuestData().getFinishCond().stream()
            .filter(cond -> cond.getType() == QuestContent.QUEST_CONTENT_ENTER_DUNGEON)
            .map(cond -> GameData.getDungeonEntriesMap().get(cond.getParam()[0]))
            .forEach(entries -> {
                // we need to enter this dungeon
                if(quest.state == QuestState.QUEST_STATE_UNFINISHED) {
                    this.plotDungeonEntries.computeIfAbsent(
                        entries.getEntryKey(), f -> new HashSet<>()).add(entries.getDungeonId());
                } else {
                    // we probably finished or reset this quest, remove the entry
                    this.plotDungeonEntries.computeIfPresent(
                        entries.getEntryKey(), (k, v) -> {
                            v.remove(entries.getDungeonId());
                            return v;
                        });
                }
            });
    }

    public DungeonEntryInfo toProto(DungeonData data) {
        val proto = new DungeonEntryInfo(data.getId(), getDungeonEntryItem().getPassedDungeons().contains(data.getId()));
        Optional.ofNullable(GameData.getDungeonSerialDataMap().get(data.getSerialId()))
            .map(DungeonSerialData::getId).map(getBossRecordMap()::get)
            .ifPresent(bossRecord -> {
                proto.setWeeklyBossResinDiscountInfo(bossRecord.toProto());
                proto.setBossChestNum(bossRecord.getTakeNum());
                proto.setMaxBossChestNum(bossRecord.getMaxTakeNumLimit());
                proto.setNextRefreshTime(bossRecord.getNextRefreshTime());
            });

        return proto;
    }

    /**
     * Record weekly boss information after completing dungeon
     * */
    public void updateDungeonEntryInfo(DungeonData dungeonData){
        getDungeonEntryItem().addDungeon(dungeonData.getId());
        getDungeonEntryItem().updateWeeklyBossInfo(dungeonData.getSerialId());
    }

    public Set<Integer> getPlotDungeonById(int sceneId, int pointId){
        return this.plotDungeonEntries.get((sceneId << 16) + pointId);
    }

    public WeeklyBossResinDiscountInfo getWeeklyBossDiscountInfo(DungeonData dungeonData) {
        return Optional.ofNullable(getBossRecordMap().get(dungeonData.getSerialId()))
            .filter(WeeklyBossRecord::canTakeReward).map(WeeklyBossRecord::toProto)
            .orElse(null);
    }

    /**
     * Get dungeon information for specific entries
     * */
    public List<DungeonData> getDungeonEntries(int sceneId, int pointId) {
        // basic dungeon entries including daily domains and weekly bosses, not including Azhdaha here
        val basicDungeons = Optional.ofNullable(GameData.getScenePointEntryById(sceneId, pointId))
            .map(ScenePointEntry::getPointData).map(PointData::getDungeonIds).stream().parallel()
            .flatMapToInt(Arrays::stream).mapToObj(GameData.getDungeonDataMap()::get)
            .filter(Objects::nonNull).toList();

        // quest entries
        val plotDungeons = Optional.ofNullable(getPlotDungeonById(sceneId, pointId)).stream().parallel()
            .flatMap(Set::stream).map(dungeonId -> GameData.getDungeonDataMap().get(dungeonId.intValue()))
            .filter(Objects::nonNull).toList();

        // dungeon that will rotate regularly, like Azhdaha weekly boss
        val rosterDungeons = Optional.ofNullable(GameData.getScenePointEntryById(sceneId, pointId))
            .map(ScenePointEntry::getPointData).map(PointData::getDungeonRosterList).stream().parallel()
            .flatMapToInt(Arrays::stream).mapToObj(GameData.getDungeonRosterDataMap()::get).filter(Objects::nonNull)
            .map(rosterData -> getBossRecordMap().values().stream().parallel().map(WeeklyBossRecord::getRosterCycleRecord)
                .filter(Objects::nonNull).map(RosterCycleRecord::getSelectedPool)
                .map(rosterData.getRosterPool()::get).filter(Objects::nonNull).toList())
            .flatMap(List::stream).map(DungeonRosterData.RosterPool::getDungeonList).flatMapToInt(Arrays::stream)
            .mapToObj(GameData.getDungeonDataMap()::get).toList();

        return Stream.of(basicDungeons, plotDungeons, rosterDungeons).flatMap(Collection::stream)
            .filter(data -> this.player.getLevel() >= data.getLimitLevel())
            .filter(data -> data.getId() != 69) // TODO, this dungeon is causing problem, find out why
            .toList();
    }
}
