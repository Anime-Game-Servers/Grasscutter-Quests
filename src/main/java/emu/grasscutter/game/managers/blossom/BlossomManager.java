package emu.grasscutter.game.managers.blossom;

import dev.morphia.annotations.Entity;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.BlossomGroupsData;
import emu.grasscutter.data.excels.BlossomOpenData;
import emu.grasscutter.data.excels.BlossomRefreshData;
import emu.grasscutter.data.excels.BlossomRefreshData.RefreshCond;
import emu.grasscutter.data.excels.BlossomSectionOrderData;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.managers.blossom.enums.BlossomRefreshType;
import emu.grasscutter.game.player.BasePlayerDataManager;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.ActionReason;
import emu.grasscutter.server.packet.send.PacketBlossomBriefInfoNotify;
import emu.grasscutter.server.packet.send.PacketWorldOwnerBlossomScheduleInfoNotify;
import emu.grasscutter.utils.Utils;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.anime_game_servers.gi_lua.models.ScriptArgs;
import org.anime_game_servers.gi_lua.models.constants.EventType;
import org.anime_game_servers.multi_proto.gi.messages.blossom.BlossomBriefInfo;
import org.anime_game_servers.multi_proto.gi.messages.gadget.BlossomChestInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Getter
@Entity
@SuppressWarnings("SpellCheckingInspection")
public class BlossomManager extends BasePlayerDataManager {
    /**
     * Holds information of all blossom camps, [Key: GroupId, Value: BlossomSchedule]
     * */
    private final ConcurrentHashMap<Integer, BlossomSchedule> blossomSchedule;
    /**
     * Holds information of all spawned chest after finishing blossom challenge,
     * necessary for building chest brief info,
     * [Key: ConfigId, Value: Blossom Group id]
     * */
    private final ConcurrentHashMap<Integer, Integer> spawnedChest;

    public BlossomManager() {
        this.blossomSchedule = new ConcurrentHashMap<>();
        this.spawnedChest = new ConcurrentHashMap<>();
    }

    public BlossomManager(Player player) {
        this();
        setPlayer(player);
    }

    /**
     * BlossomBriefInfo proto
     * */
    public List<BlossomBriefInfo> getBriefInfo() {
        return this.blossomSchedule.values().stream()
            .map(BlossomSchedule::toBriefProto)
            .sorted(Comparator.comparing(BlossomBriefInfo::getRefreshId)
                .thenComparing(BlossomBriefInfo::getCircleCampId))
            .toList();
    }

    /**
     * Notify player's(not necessary world owner) all BlossomBriefInfo (all including not spawned)
     * */
    public void notifyPlayerIcon() {
        this.player.sendPacket(new PacketBlossomBriefInfoNotify(getBriefInfo()));
    }

    /**
     * Create all blossom camps and send packets
     * */
    public void onPlayerLogin() {
        if (this.blossomSchedule.isEmpty()) buildBlossomSchedule();
        notifyPlayerIcon();
    }

    /**
     * Reset blossom schedules, some blossoms have different reset time, so its neccessary to check it onTick
     * */
    public void dailyReset() {
        val schedulesToReset = this.blossomSchedule.values().stream().filter(BlossomSchedule::shouldReset)
            .map(BlossomSchedule::getGroupId).toList();

        if (!schedulesToReset.isEmpty()) {
            val schedulesRefreshData = schedulesToReset.stream().map(this.blossomSchedule::remove)
                .filter(Objects::nonNull).map(BlossomSchedule::getRefreshId)
                .map(refreshId -> GameData.getBlossomRefreshDataMap().get(refreshId.intValue())).distinct().toList();
            buildBlossomSchedule(schedulesRefreshData);
            val chestToRemove = this.spawnedChest.entrySet().stream()
                .filter(e -> schedulesToReset.contains(e.getValue()))
                .map(Map.Entry::getKey).toList();
            chestToRemove.forEach(this.spawnedChest::remove);
            // TODO might also have to trigger scripts call
            notifyPlayerIcon();
        }
    }

    /**
     * Update Blossom camp's state (started or ended blossom challenge for example), triggered by ScriptLib
     * */
    public boolean setBlossomState(int groupId, int state) {
        val scheduleOption = Optional.ofNullable(this.blossomSchedule.get(groupId));
        if (scheduleOption.isPresent()) {
            scheduleOption.get().setState(state);
            this.player.getScene().broadcastPacket(new PacketWorldOwnerBlossomScheduleInfoNotify(scheduleOption.get().toScheduleProto()));
        }
        return scheduleOption.isPresent();
    }

    /**
     * Update Blossom camp's progress (on monster die for example), triggered by ScriptLib
     * */
    public boolean addBlossomProgress(int groupId) {
        val scheduleOption = Optional.ofNullable(this.blossomSchedule.get(groupId));
        if (scheduleOption.isPresent()) {
            val schedule = scheduleOption.get();
            schedule.addProgress();
            this.player.getScene().broadcastPacket(
                new PacketWorldOwnerBlossomScheduleInfoNotify(schedule.toScheduleProto()));
            if (schedule.isFinished()) this.player.getScene().getScriptManager().callEvent(
                new ScriptArgs(schedule.getGroupId(), EventType.EVENT_BLOSSOM_PROGRESS_FINISH));
        }
        return scheduleOption.isPresent();
    }

    /**
     * Get Blossom Chest gadget proto info (after challenge finish)
     * */
    public BlossomChestInfo getChestInfo(int chestConfigId, List<Integer> playersUid) {
        val scheduleOption = Optional.ofNullable(this.spawnedChest.get(chestConfigId))
            .map(this.blossomSchedule::get);
        scheduleOption.ifPresent(schedule -> schedule.getRemainingUid().addAll(playersUid));
        return scheduleOption.map(schedule -> {
            val proto = new BlossomChestInfo();

            proto.setQualifyUidList(playersUid);
            proto.setRemainUidList(playersUid);
            proto.setResin(schedule.getResin());
            proto.setRefreshId(schedule.getRefreshId());
            proto.setBlossomRefreshType(schedule.getRefreshType().getValue());
            return proto;
        }).orElse(null);
    }

    /**
     * Give player reward after challenge finish
     * */
    public boolean onReward(Player player, @NonNull EntityGadget gadget, boolean useCondensedResin) {
        val scheduleOption = Optional.ofNullable(this.spawnedChest.get(gadget.getConfigId()))
            .map(this.blossomSchedule::get);
        // if player is not qualified for the reward
        if (scheduleOption.filter(s -> s.getRemainingUid().contains(player.getUid())).isEmpty()) return false;

        val schedule = scheduleOption.get();
        // give rewards
        val resinManager = player.getResinManager();
        val payable = useCondensedResin ? resinManager.useCondensedResin(1) : resinManager.useResin(schedule.getResin());
        if (!payable) return false;

        val blossomRewards = GameData.getRewardPreviewDataMap().get(schedule.getRewardId());
        if (blossomRewards == null) return false;

        player.getInventory().addItems(Stream.of(blossomRewards.getPreviewItems())
            .map(reward -> new GameItem(reward.getItemId(), reward.getCount() * (useCondensedResin ? 2 : 1)))
            .toList(), ActionReason.OpenBlossomChest);

        schedule.getRemainingUid().remove(player.getUid());
        return true;
    }

    /**
     * Build new blossom schedule using information from previous schedule
     * */
    public void buildNextCamp(int groupId) {
        val schedule = this.blossomSchedule.remove(groupId);
        if(schedule == null) return;
        val world = player.getWorld();

        Optional.ofNullable(GameData.getBlossomGroupsDataMap().get(schedule.getCircleCampId()))
            .map(BlossomGroupsData::getNextCampId)
            .map(campId -> GameData.getBlossomGroupsDataMap().get(campId.intValue()))
            // if next camp overlaps with existing schedule, get further next camp
            .map(groupsData -> this.blossomSchedule.containsKey(groupsData.getNewGroupId()) ?
                GameData.getBlossomGroupsDataMap().get(groupsData.getNextCampId()) : groupsData).stream()
            .map(groupData -> BlossomSchedule.create(world, schedule, groupData, getWorldLevel()))
            .filter(Objects::nonNull)
//            .peek(newSchedule -> Grasscutter.getLogger().info("[BlossomManager] New {}", newSchedule))
            .forEach(newSchedule -> {
                this.blossomSchedule.put(newSchedule.getGroupId(), newSchedule);
                this.player.getScene().runWhenFinished(() -> {
                    this.player.getScene().loadDynamicGroup(newSchedule.getGroupId());
                    val decorateGroupId = newSchedule.getDecorateGroupId();
                    if(decorateGroupId != 0) {
                        Grasscutter.getLogger().debug("[BlossomManager] New Decorate Group: {}", decorateGroupId);
                        this.player.getScene().loadDynamicGroup(decorateGroupId);
                    }
                });
            });
        notifyPlayerIcon(); // notify all camps again
    }

    /**
     * Rebuild all Blossom Camp gadget (not chest gadget) for scene
     * */
    public void loadBlossomGroup() {
        val scene = this.player.getScene();
        if (scene == null) return;

        this.blossomSchedule.values().stream()
            .filter(schedule -> schedule.getSceneId() == scene.getId())
            .forEach(schedule -> {
                scene.loadDynamicGroup(schedule.getGroupId());
                val decorateGroupId = schedule.getDecorateGroupId();
                if(decorateGroupId != 0) {
                    Grasscutter.getLogger().debug("[BlossomManager] Decorate Group: {}", decorateGroupId);
                    scene.loadDynamicGroup(decorateGroupId);
                }
            });
//            .peek(schedule -> Grasscutter.getLogger().info("[Blossom Manager] Loading Blossom Group: {}", schedule.getGroupId()))
    }

    private int getWorldLevel() {
        return this.player.getWorldLevel(); // maybe should get the owner's?
    }

    private int getPlayerLevel() {
        return this.player.getLevel(); // maybe should get the owner's?
    }

    /**
     * Check if player is qualified to open certain blossom camp
     * */
    private boolean refreshCondMet(@NonNull RefreshCond cond) {
        return switch (cond.getType()) {
            case BLOSSOM_REFRESH_COND_NONE -> true;
            case BLOSSOM_REFRESH_COND_PLAYER_LEVEL_EQUAL_GREATER ->
                cond.getParam().stream().allMatch(p -> getPlayerLevel() >= p);
            case BLOSSOM_REFRESH_COND_PLAYER_LEVEL_LESS_THAN ->
                cond.getParam().stream().allMatch(p -> getPlayerLevel() < p);
            case BLOSSOM_REFRESH_COND_OPEN_STATE ->
                cond.getParam().stream().allMatch(p -> this.player.getProgressManager().getOpenState(p) == 1);
            // case BLOSSOM_REFRESH_COND_ACTIVITY_COND, BLOSSOM_REFRESH_COND_SCENE_TAG_ADDED -> TODO
            default -> false;
        };
    }

    /**
     * Build all blossom camp information
     * */
    private void buildBlossomSchedule() {
        buildBlossomSchedule(GameData.getBlossomRefreshDataMap().values());
    }

    private void buildBlossomSchedule(Collection<BlossomRefreshData> refreshData) {
        val appendedGroupId = new ArrayList<>(); // counter to avoid repeated groups and section
        val appendedSectionId = new ArrayList<>();

        refreshData.stream().filter(data -> data.getRefreshCondVec().stream().allMatch(this::refreshCondMet))
            .filter(data -> Optional.ofNullable(GameData.getBlossomOpenDataMap().get(data.getCityId()))
                .map(BlossomOpenData::getOpenLevel).filter(openLevel -> getPlayerLevel() >= openLevel).isPresent())
            .filter(data -> data.getRefreshType() != BlossomRefreshType.BLOSSOM_REFRESH_NONE) // unimplemented types
            .map(data -> { // get section order for the current city
                val groupList = new ArrayList<BlossomGroupsData>(); // groups that met condition
                return Stream.ofNullable(GameData.getBlossomSectionOrderDataMap().values().stream()
                        .filter(orderData -> orderData.getCityId() == data.getCityId())
                        .map(BlossomSectionOrderData::getSectionId)
                        .filter(sectionId -> GameData.getBlossomGroupsDataMap().values().stream() // some section does not have groups
                            .anyMatch(groupData -> groupData.getSectionId() == sectionId))
                        .filter(sectionId -> !appendedSectionId.contains(sectionId)) // don't want to get the same section
                        .filter(sectionId -> Stream.ofNullable(GameData.getBlossomGroupsDataMap().values().stream()
                                .filter(group -> group.getCityId() == data.getCityId())
                                .filter(group -> group.getSectionId() == sectionId)
                                .filter(group -> group.getRefreshTypeVec().contains(data.getRefreshType().getValue()))
                                .filter(group -> !appendedGroupId.contains(group.getId()))
                                .filter(BlossomGroupsData::isInitialRefresh).toList())
                            .filter(tempGroupList -> tempGroupList.size() >= data.getRefreshCount())
                            .peek(groupList::addAll).findFirst().isPresent()).toList())
                    .filter(sectionList -> !sectionList.isEmpty()).map(Utils::drawRandomListElement)
                    .peek(appendedSectionId::add) // draw a new random section id
                    .map(sectionList -> IntStream.range(0, data.getRefreshCount()).mapToObj(e ->
                            Stream.ofNullable(groupList).filter(gl -> !gl.isEmpty()).map(Utils::drawRandomListElement)// draw a new random group
                                .peek(randomGroup -> appendedGroupId.add(randomGroup.getId())).peek(groupList::remove)
                                .map(randomGroup -> BlossomSchedule.create(player.getWorld(), data, randomGroup, getWorldLevel())) // build blossom schedule
                                .filter(Objects::nonNull).findFirst().orElse(null))
                        .filter(Objects::nonNull).toList())
                    .findFirst().orElse(null);
            }).filter(Objects::nonNull).flatMap(List::stream).forEach(schedule -> this.blossomSchedule.put(schedule.getGroupId(), schedule));
    }
}
