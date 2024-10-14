package emu.grasscutter.game.tower;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.common.ItemParamData;
import emu.grasscutter.data.excels.*;
import emu.grasscutter.game.avatar.TowerAvatar;
import emu.grasscutter.game.dungeons.DungeonManager;
import emu.grasscutter.game.dungeons.settle_listeners.DungeonSettleListener;
import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import emu.grasscutter.game.dungeons.challenge.trigger.GuardTrigger;
import emu.grasscutter.game.dungeons.challenge.trigger.TimeTrigger;
import emu.grasscutter.game.dungeons.settle_listeners.TowerDungeonSettleListener;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.player.BasePlayerManager;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.ActionReason;
import emu.grasscutter.game.props.EnterReason;
import emu.grasscutter.game.world.data.TeleportProperties;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.rotation.TowerCurLevelRecord;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.TowerTeam;
import emu.grasscutter.server.packet.send.*;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.scene.EnterType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO record tower combat record, the highest burst count, highest atk etc
 * TODO tower challenge state show in player personal page
 * */
public class TowerManager extends BasePlayerManager {
    private static final DungeonSettleListener TOWER_DUNGEON_SETTLE_LISTENER = new TowerDungeonSettleListener();
    private static final int STAR_COUNT_TO_UNLOCK_SCHEDULE_FLOOR = 6;

    public TowerManager(Player player) {
        super(player);
    }

    public TowerData getTowerData() {
        return this.player.getTowerData();
    }

    public TowerCurLevelRecord getCurRecordProto() {
        return getTowerData().toProto();
    }

    private TowerLevelRecordInfo getCurLevelRecordInfo() {
        return Optional.ofNullable(getCurFloorRecordInfo()).map(TowerFloorRecordInfo::getPassedLevelRecordMap)
            .map(passedMap -> passedMap.get(getCurrentLevelIndex() - 1)).orElse(null);
    }

    private TowerFloorRecordInfo getCurFloorRecordInfo() {
        return Optional.ofNullable(getCurFloorData()).map(TowerFloorData::getFloorIndex)
            .map(getRecordMap()::get).orElse(null);
    }

    private TowerFloorData getCurFloorData() {
        return GameData.getTowerFloorDataMap().get(getTowerData().getCurrentFloorId());
    }

    private TowerLevelData getCurLevelData() {
        return GameData.getTowerLevelDataMap().get(getTowerData().getCurrentLevelId());
    }

    private int getCurrentLevelIndex() {
        return getTowerData().getCurrentLevelIndex();
    }

    public Map<Integer, TowerFloorRecordInfo> getRecordMap() {
        return getTowerData().getFloorRecordMap();
    }

    private TowerSystem getTowerSystem() {
        return this.player.getServer().getTowerSystem();
    }

    public TowerMonthlyBriefInfo getMonthlyBriefInfo() {
        return getTowerData().getMonthBriefInfo();
    }

    public void onLogin() {
        // unlock first floor by default, TODO maybe should also check against player level for more completed gameplay
        if (getRecordMap().isEmpty()) {
            getRecordMap().put(1, TowerFloorRecordInfo.create(1001));
        }

        // check if there is new schedule, refresh floor 9 to 12
        if (getTowerSystem().getScheduleId() != getTowerData().getScheduleId()) {
            // can't really check against schedule end time, since it would probably be the new schedule end time at this point
            getTowerData().startNewSchedule(getTowerSystem().getScheduleId());
            if (canEnterScheduleFloor()) {
                getRecordMap().put(9, TowerFloorRecordInfo.create(getTowerSystem().getFirstScheduleFloor()));
            }
        }
    }

    // Packet notifications
    private void notifyAddBuffs(int towerBuffId) {
        Optional.ofNullable(GameData.getTowerBuffDataMap().get(towerBuffId))
            .ifPresent(towerBuffData -> this.player.getBuffManager().addBuff(towerBuffData.getBuffId()));
    }

    public void notifyCurRecordChange() {
        this.player.getSession().send(new PacketTowerCurLevelRecordChangeNotify(getCurRecordProto()));
    }

    public void notifyFloorChange(TowerFloorRecordInfo floorRecordInfo) {
        notifyFloorChange(List.of(floorRecordInfo));
    }

    public void notifyFloorChange(List<TowerFloorRecordInfo> floorRecordInfoList) {
        this.player.sendPacket(new PacketTowerFloorRecordChangeNotify(floorRecordInfoList, canEnterScheduleFloor()));
    }

    // Team related
    /**
     * Invokes whenever player chooses a team and enter spiral abyss
     * */
    public boolean teamSelect(int floorId, List<TowerTeam> towerTeamInfo) {
        val floorData = GameData.getTowerFloorDataMap().get(floorId);
        if (floorData == null) return false;

        // should initialise the records for current selection
        getTowerData().initCurLevelRecord(towerTeamInfo, this.player, floorData);
        return true;
    }

    /**
     * Use tower teams that player has selected
     * */
    private void rebuildAndUseTeam(int teamId) {
        // it will be empty if the player quit and resume the chamber
        if (this.player.getTeamManager().getTemporaryTeam().isEmpty()) {
            // recorded avatars should not be empty at this point, create a temporary copy of the record
            // try to add avatars to storage first if it hasn't already been done
            val guidListList = getTowerData().getTeamOnHold().stream().peek(TowerTeamInfo::copy)
                .map(TowerTeamInfo::getTempAvatars).peek(avatarList -> avatarList.stream()
                    .filter(this.player.getAvatars()::addAvatar).peek(TowerAvatar::equipItems)
                    .forEach(avatar -> this.player.sendPacket(new PacketAvatarAddNotify(avatar, false))))
                .map(avatarList -> avatarList.stream().map(TowerAvatar::getGuid).toList()).toList();

            getTowerData().updateTowerTeamStats();
            this.player.getTeamManager().setupTemporaryTeam(guidListList);
        } else {
            if (teamId == 0) { // means that player is continuing to next level
                val guidListList = getTowerData().getTeamOnHold().stream().map(TowerTeamInfo::getTempAvatars)
                    .map(avatarList -> avatarList.stream().map(TowerAvatar::getGuid).toList()).toList();
                // TODO, mainly wants to rebuild entity guid map, so probably don't have to rebuild entire team
                this.player.getTeamManager().setupTemporaryTeam(guidListList);
            }
        }
        this.player.getTeamManager().useTemporaryTeam(teamId);
    }

    /**
     * Change to second team for lower half of abyss, triggered by scripts
     * */
    public void mirrorTeamSetUp(int teamId) {
        getTowerData().setUpperPart(false);
        this.player.sendPacket(new PacketTowerMiddleLevelChangeTeamNotify());

        val dungeonData = Optional.ofNullable(this.player.getScene().getDungeonManager()).map(DungeonManager::getDungeonData);
        val teleportProps = TeleportProperties.builder()
            .sceneId(this.player.getSceneId())
            .enterReason(EnterReason.LuaSkipUi)
            .enterType(EnterType.ENTER_GOTO)
            .dungeonId(dungeonData.map(DungeonData::getId).orElse(0))
            .prevPos(this.player.getPosition())
            .prevSceneId(this.player.getSceneId())
            .worldType(dungeonData.map(data -> 13).orElse(14))
            .isSkipUi(true);

        // this function should
        this.player.getWorld().transferPlayerToScene(this.player, teleportProps.build());
        rebuildAndUseTeam(teamId);
    }

    public void clearTeamOnExit() {
        this.player.getTeamManager().cleanTemporaryTeam();
        this.player.sendPacket(new PacketAvatarDelNotify(getTowerData().getTeamOnHold().stream().map(TowerTeamInfo::getAvatars)
            .flatMap(List::stream).map(TowerAvatar::getGuid).peek(this.player.getAvatars()::removeAvatarByGuid).toList()));
    }

    // Gameplay related
    public void resetCurRecord() {
        this.player.sendPacket(new PacketAvatarDelNotify(getTowerData().getTeamOnHold().stream().map(TowerTeamInfo::getAvatars)
            .flatMap(List::stream).map(TowerAvatar::getGuid).peek(this.player.getAvatars()::removeAvatarByGuid).toList()));
        getTowerData().reset(); // player should be choosing new team at this point, should remove old team
    }

    public void updateNextLevel(DungeonManager manager) {
        // don't update if this is only upper half of the challenge
        if (manager == null || !manager.isFinishedSuccessfully() || getTowerData().isUpperPart()) return;

        if (hasNextLevel()) {
            getTowerData().syncToNextLevel(getNextLevelId());
            getTowerData().updateTowerTeamStats();
        } else { // get next floor if reached end level of this floor
            Optional.ofNullable(GameData.getTowerFloorDataMap().get(getNextFloorId()))
                .ifPresent(floorData -> getRecordMap().putIfAbsent(
                    floorData.getFloorIndex(), TowerFloorRecordInfo.create(floorData.getId())));
            resetCurRecord();
        }
        notifyCurRecordChange();
    }

    /**
     * Calculate the result after clearing level
     * */
    public int calculateStar(DungeonManager manager, WorldChallenge challenge) {
        val levelData = getCurLevelData();
        if (levelData == null || manager == null ||challenge == null) return 0;

        int stars = (int) levelData.getConds().stream()
            .map(c -> getTowerData().isUpperPart() ? c.getUpperHalfCond() : c.getLowerHalfCond())
            .filter(c -> switch (c.getTowerCondType()) { // TODO, maybe worth it to create quest like handler to adapt future changes?
                case TOWER_COND_CHALLENGE_LEFT_TIME_MORE_THAN ->
                    Optional.ofNullable(challenge.getChallengeTriggers().get(TimeTrigger.class))
                        .filter(tt -> challenge.getStartedAt() + tt.getGoal().get() - challenge.getFinishedTime() >= c.getTargetLeftTime())
                        .isPresent();
                case TOWER_COND_LEFT_HP_GREATER_THAN -> challenge.getGroupId() == c.getGroupId() &&
                    Optional.ofNullable(challenge.getChallengeTriggers().get(GuardTrigger.class))
                        .filter(gt -> gt.getGoal().get() == c.getConfigId())
                        .filter(gt -> ((GuardTrigger) gt).getLastSendPercent() >= c.getTargetHpPercentage())
                        .isPresent();
            }).count();

        tryRemoveBuffs();
        if (manager.isFinishedSuccessfully()) { // always update only if
            getTowerData().updateFloorRecord(stars); // will not update if the current result is not better than recorded
            notifyFloorChange(getCurFloorRecordInfo());
        } else {
            removeCurrentLevelBuff();
            getTowerData().setUpperPart(true); // always starts from upper half on wrap up
        }

        return stars;
    }

    /**
     * Trigger when entering a chamber/level
     * */
    public void enterLevel(int enterPointId) {
        val levelData = getCurLevelData();
        if (levelData == null) return;

        val isRestart = Optional.ofNullable(this.player.getScene().getDungeonManager())
            .map(DungeonManager::getDungeonData).map(DungeonData::getId).filter(id -> id == levelData.getDungeonId())
            .isPresent();
        if (isRestart) { // start from lower half if player restarts dungeon
            getTowerData().setUpperPart(true);
            clearTeamOnExit();
            this.player.getServer().getDungeonSystem().restartDungeon(this.player, TOWER_DUNGEON_SETTLE_LISTENER);
        } else {
            this.player.getServer().getDungeonSystem().enterDungeon(
                this.player, enterPointId, levelData.getDungeonId(), TOWER_DUNGEON_SETTLE_LISTENER);
        }

        rebuildAndUseTeam(getTowerData().getTeamIndex()); // use team user choose or recorded
        notifyCurRecordChange();
        getTowerData().getTowerBuffs().values().forEach(this::notifyAddBuffs); // notify any recorded buffs

        this.player.getSession().send(new PacketTowerEnterLevelRsp(getTowerData()));
        // stop using skill
        this.player.getSession().send(new PacketCanUseSkillNotify(false));
        // notify the cond of stars
        this.player.getSession().send(new PacketTowerLevelStarCondNotify(getTowerData()));
    }

    private int getNextLevelId() {
        return Optional.ofNullable(getCurFloorData()).map(TowerFloorData::getLevelGroupId)
            .map(floorDataGroupId -> GameData.getTowerLevelDataMap().values().stream()
                .filter(levelData -> levelData.getLevelGroupId() == floorDataGroupId)
                .filter(levelData -> levelData.getLevelIndex() > getCurrentLevelIndex())
                .map(TowerLevelData::getId).sorted().findFirst())
            .flatMap(innerOption -> innerOption).orElse(0);
    }

    /**
     * Check there is next level (return false when player reached chamber 3 of current floor),
     * should go to next floor at this point
     * */
    public boolean hasNextLevel() {
        return getNextLevelId() > 0;
    }

    public int getNextFloorId() {
        return getTowerSystem().getNextFloorId(getTowerData().getCurrentFloorId());
    }

    /**
     * Check there is next floor (return false when player reached floor 12)
     * */
    public boolean hasNextFloor() {
        return getNextFloorId() > 0;
    }

    /**
     * Check if player can enter floor 9
     * */
    public boolean canEnterScheduleFloor() {
        return getRecordMap().values().stream().filter(record -> record.getStarCount() >= STAR_COUNT_TO_UNLOCK_SCHEDULE_FLOOR)
            .anyMatch(record -> record.getFloorId() == getTowerSystem().getLastEntranceFloor());
    }

    // Buffs related
    public void addBuffs(int towerBuffId) {
        getTowerData().getTowerBuffs().put(getCurrentLevelIndex(), towerBuffId);
        notifyAddBuffs(towerBuffId);
    }

    /**
     * Trigger when player exit without completing current level
     * */
    public void removeCurrentLevelBuff() {
        getTowerData().getTowerBuffs().remove(getCurrentLevelIndex());
    }

    /**
     * Check if it should remove buffs when moving to new level or new floor
     * */
    private void tryRemoveBuffs() {
        val towerBuffList = getTowerData().getTowerBuffs().values().stream().map(towerBuffId ->
                GameData.getTowerBuffDataMap().get(towerBuffId.intValue()))
            .filter(Objects::nonNull).filter(buffData ->
                buffData.getLastingType() == TowerBuffLastingType.TOWER_BUFF_LASTING_LEVEL
                    || buffData.getLastingType() == TowerBuffLastingType.TOWER_BUFF_LASTING_IMMEDIATE)
            .map(TowerBuffData::getId).toList();

        towerBuffList.forEach(getTowerData().getTowerBuffs()::remove);
        towerBuffList.stream().map(towerBuffId -> GameData.getTowerBuffDataMap().get(towerBuffId.intValue()))
            .filter(Objects::nonNull).forEach(towerBuffData ->
                this.player.getBuffManager().removeBuff(towerBuffData.getBuffId()));
    }

    // Rewards related
    /**
     * Get start reward, or namely Star bounty
     * */
    public boolean getStarReward(int floorId) {
        val floorData = GameData.getTowerFloorDataMap().get(floorId);
        if (floorData == null) return false;

        val recordInfo = getRecordMap().get(floorData.getFloorIndex());
        if (recordInfo == null) return false;

        val rewardIds = recordInfo.getPassedLevelMap().keySet().stream().parallel()
            .map(integer -> GameData.getTowerLevelDataMap().get(integer.intValue()))
            .filter(Objects::nonNull).map(TowerLevelData::getLevelIndex)
            .map(levelIndex -> GameData.getTowerRewardData(levelIndex, floorData.getFloorIndex()))
            .filter(Objects::nonNull).map(rewardData -> rewardData.getStarRewardsByStarCount(recordInfo.getStarCount()))
            .flatMap(List::stream).filter(rewardId -> !recordInfo.getReceivedStarBounty().contains(rewardId)).distinct().toList();

        val rewardItems = rewardIds.stream().parallel().map(rewardId -> GameData.getRewardDataMap().get(rewardId.intValue()))
            .filter(Objects::nonNull).map(RewardData::getRewardItemList).flatMap(List::stream)
            .collect(Collectors.toMap(ItemParamData::getItemId, ItemParamData::getItemCount, Integer::sum))
            .entrySet().stream().map(e -> new GameItem(e.getKey(), e.getValue()))
            .sorted(Comparator.comparing(GameItem::getItemId)).toList();

        if (!rewardItems.isEmpty()) {
            recordInfo.onGetReward(rewardIds);
            notifyFloorChange(recordInfo);
            this.player.getInventory().addItems(rewardItems, ActionReason.TowerFloorStarReward);
        }

        return !rewardItems.isEmpty();
    }

    /**
     * Give tower first pass reward, namely chamber rewards
     * */
    public List<GameItem> giveFirstPassReward(DungeonManager manager) {
        if (!manager.isFinishedSuccessfully() || getCurLevelRecordInfo().isReceivedFirstPassReward()) return List.of();

        val rewardItems = Optional.ofNullable(getCurFloorData()).map(TowerFloorData::getFloorIndex)
            .map(floorIndex -> GameData.getTowerRewardData(getCurrentLevelIndex(), floorIndex))
            .map(rewardData -> rewardData.getFirstPassRewardByStarCount(getCurFloorRecordInfo().getStarCount()))
            .stream().flatMap(List::stream).distinct().map(rewardId -> GameData.getRewardDataMap().get(rewardId.intValue()))
            .filter(Objects::nonNull).map(RewardData::getRewardItemList).flatMap(List::stream)
            .collect(Collectors.toMap(ItemParamData::getItemId, ItemParamData::getItemCount, Integer::sum))
            .entrySet().stream().map(e -> new GameItem(e.getKey(), e.getValue()))
            .sorted(Comparator.comparing(GameItem::getItemId)).toList();

        if (!rewardItems.isEmpty()) {
            getCurLevelRecordInfo().setReceivedFirstPassReward(true);
            this.player.getInventory().addItems(rewardItems, ActionReason.TowerFirstPassReward);
        }

        return rewardItems;
    }
}
