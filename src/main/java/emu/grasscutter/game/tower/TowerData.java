package emu.grasscutter.game.tower;

import dev.morphia.annotations.Entity;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.TowerFloorData;
import emu.grasscutter.data.excels.TowerLevelData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.utils.Utils;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.rotation.TowerCurLevelRecord;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.rotation.TowerFloorRecord;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.TowerTeam;

import java.util.*;


/**
 * Spiral abyss information
 * domain in form of 12-1, 12-2, 12-3 for example
 * 12 is floor id, 1, 2 and 3 is level index
 * */
@Entity @Getter
public class TowerData{
    /**
     * the floor players chose
     */
    private int currentFloorId;
    /**
     * Used to get game data
     * */
    private int currentLevelId;
    /**
     * the level player currently in
     * */
    private int currentLevelIndex;
    /**
     * whether it is upper or lower half of the level
     * */
    @Setter private boolean isUpperPart;
    /**
     * player's tower team
     * */
    private final List<TowerTeamInfo> teamOnHold = new ArrayList<>();
    /**
     * player selected buffs
     * entry: [floorIndex, TowerBuffId]
     * */
    private final Map<Integer, Integer> towerBuffs = new Int2IntArrayMap();
    /**
     * entry: [floorIndex, Record]
     */
    private final Map<Integer, TowerFloorRecordInfo> floorRecordMap = new Int2ObjectOpenHashMap<>();
    /**
     * Holds current scheduleId
     * */
    private int scheduleId;
    /**
     * Shows best progress of previously scheduled tower
     */
    private TowerMonthlyBriefInfo monthlyBriefInfo;

    public void initCurLevelRecord(List<TowerTeam> newTowerTeam, Player player, TowerFloorData floorData) {
        this.isUpperPart = true;
        this.towerBuffs.clear();
        addNewTeam(newTowerTeam, player);
        this.currentFloorId = floorData.getId();
        // it is safe to assume that level index is 1 whenever this function is occur
        this.currentLevelIndex = 1;
        this.currentLevelId = GameData.getTowerLevelDataMap().values().stream()
            .filter(x -> x.getLevelGroupId() == floorData.getLevelGroupId() && x.getLevelIndex() == 1)
            .findFirst()
            .map(TowerLevelData::getId)
            .orElse(0);
    }

    public void reset() {
        this.isUpperPart = false;
        this.teamOnHold.clear();
        this.towerBuffs.clear();
        this.currentFloorId = 0;
        this.currentLevelIndex = 0;
        this.currentLevelId = 0;
    }

    public void addNewTeam(List<TowerTeam> newTowerTeam, Player player) {
        this.teamOnHold.clear();
        this.teamOnHold.addAll(newTowerTeam.stream()
            .map(towerTeam -> TowerTeamInfo.create(towerTeam, player))
            .toList());
    }

    public void updateTowerTeamStats() {
        this.teamOnHold.forEach(TowerTeamInfo::update);
    }

    // it is safe to assume there is only two teams in abyss
    public int getTeamIndex() {
        return this.isUpperPart ? 0 : 1;
    }

    public List<Integer> getRandomBuffs() {
        return Optional.ofNullable(GameData.getTowerLevelDataMap().get(this.currentLevelId))
            .map(TowerLevelData::getTowerBuffConfigFloorList).stream()
            .flatMap(List::stream)
            .filter(levelBuffList -> !levelBuffList.getConfigList().isEmpty())
            .map(levelBuffList -> {
                val configList = levelBuffList.getConfigList().stream().filter(buff -> !this.towerBuffs.containsValue(buff.getId())).toList();
                return Utils.drawRandomListElement(configList.stream().map(TowerLevelData.TowerBuffConfig::getId).toList(),
                    configList.stream().map(TowerLevelData.TowerBuffConfig::getWeight).toList());
            }).toList();
    }

    public void updateFloorRecord(int star) {
        Optional.ofNullable(GameData.getTowerFloorDataMap().get(this.currentFloorId)).ifPresent(floorData ->
            this.floorRecordMap.computeIfAbsent(floorData.getFloorIndex(), f -> TowerFloorRecordInfo.create(floorData.getId()))
                .update(this.currentLevelId, this.currentLevelIndex, star));
    }

    public void syncToNextLevel(int levelId) {
        val levelData = Optional.ofNullable(GameData.getTowerLevelDataMap().get(levelId));
        this.currentLevelIndex = levelData.map(TowerLevelData::getLevelIndex).orElse(0);
        this.currentLevelId = levelData.map(TowerLevelData::getId).orElse(0);
        this.isUpperPart = true;
    }

    public void startNewSchedule(int scheduleId) {
        // it should record the old schedule information
        val bestFloorIndex = this.floorRecordMap.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        val bestFloorRecord = Optional.ofNullable(this.floorRecordMap.get(bestFloorIndex));
        getMonthBriefInfo().update(
            this.scheduleId,
            bestFloorIndex,
            bestFloorRecord.map(TowerFloorRecordInfo::getBestLevelIndex).orElse(0),
            bestFloorRecord.map(TowerFloorRecordInfo::getStarCount).orElse(0));

        // update/reset new information
        this.scheduleId = scheduleId;
        // TODO maybe read off data excels to make less hard coded
        this.floorRecordMap.entrySet().removeIf(entry -> entry.getKey() >= 9 && entry.getKey() <= 12);
    }

    public List<TowerTeam> towerTeamProto(){
        return this.teamOnHold.stream().map(TowerTeamInfo::toProto).toList();
    }

    public TowerCurLevelRecord toProto() {
        val proto = new TowerCurLevelRecord();
        proto.setCurFloorId(this.currentFloorId);
        proto.setCurLevelIndex(this.currentLevelIndex);
        proto.setTowerTeamList(towerTeamProto());
        proto.setBuffIdList(this.towerBuffs.values().stream().toList());
        proto.setUpperPart(this.isUpperPart);
        proto.setEmpty(this.teamOnHold.isEmpty());
        return proto;
    }

    public List<TowerFloorRecord> getTowerRecordProtoList() {
        return this.floorRecordMap.values().stream()
            .map(TowerFloorRecordInfo::toProto)
            .sorted(Comparator.comparing(TowerFloorRecord::getFloorId))
            .toList();
    }

    public TowerMonthlyBriefInfo getMonthBriefInfo() {
        if (this.monthlyBriefInfo == null) {
            this.monthlyBriefInfo = TowerMonthlyBriefInfo.create(this.scheduleId);
        }
        return this.monthlyBriefInfo;
    }
}
