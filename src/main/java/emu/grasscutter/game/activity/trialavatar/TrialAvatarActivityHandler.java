package emu.grasscutter.game.activity.trialavatar;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.ActivityWatcherData;
import emu.grasscutter.data.excels.TrialAvatarActivityDataData;
import emu.grasscutter.game.activity.ActivityWatcher;
import emu.grasscutter.game.activity.DefaultWatcher;
import emu.grasscutter.game.dungeons.settle_listeners.TrialAvatarDungeonSettleListener;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.ActionReason;
import emu.grasscutter.game.props.WatcherTriggerType;
import emu.grasscutter.game.activity.ActivityHandler;
import emu.grasscutter.game.activity.GameActivity;
import emu.grasscutter.game.activity.PlayerActivityData;
import emu.grasscutter.game.props.ActivityType;
import emu.grasscutter.server.packet.send.PacketActivityInfoNotify;
import emu.grasscutter.server.packet.send.PacketScenePlayerLocationNotify;
import emu.grasscutter.utils.JsonUtils;

import java.util.*;
import lombok.*;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.ActivityInfo;

@GameActivity(ActivityType.NEW_ACTIVITY_TRIAL_AVATAR)
public class TrialAvatarActivityHandler extends ActivityHandler {
    @Getter private int selectedTrialAvatarIndex;
    private static final WatcherTriggerType triggerType = WatcherTriggerType.TRIGGER_FINISH_CHALLENGE;
    private static final TrialAvatarDungeonSettleListener TRIAL_AVATAR_DUNGEON_SETTLE_LISTENER = new TrialAvatarDungeonSettleListener();

    @Override
    public void onInitPlayerActivityData(@NonNull PlayerActivityData playerActivityData) {
        playerActivityData.setDetail(TrialAvatarPlayerData.create(getActivityConfigItem().getScheduleId()));
    }

    @Override
    public void onProtoBuild(PlayerActivityData playerActivityData, @NonNull ActivityInfo activityInfo) {
        activityInfo.setDetail(
            new ActivityInfo.Detail.TrialAvatarInfo(
                getTrialAvatarPlayerData(playerActivityData).toProto()
            )
        );
    }

    @Override
    public void initWatchers(@NonNull Map<WatcherTriggerType, ConstructorAccess<?>> activityWatcherTypeMap) {
        val watcher = Optional.ofNullable(activityWatcherTypeMap.get(triggerType))
            .map(ConstructorAccess::newInstance).filter(ActivityWatcher.class::isInstance)
            .map(ActivityWatcher.class::cast).orElse(new DefaultWatcher());
        watcher.setActivityHandler(this);

        getWatchersMap().computeIfAbsent(triggerType, k -> new ArrayList<>()).add(watcher);
    }

    public TrialAvatarPlayerData getTrialAvatarPlayerData(@NonNull PlayerActivityData playerActivityData) {
        if (playerActivityData.getDetail() == null || playerActivityData.getDetail().isBlank()) {
            onInitPlayerActivityData(playerActivityData);
            playerActivityData.save();
        }

        return JsonUtils.decode(playerActivityData.getDetail(), TrialAvatarPlayerData.class);
    }

    private Optional<TrialAvatarActivityDataData> getActivityData(int trialAvatarIndexId) {
        return Optional.ofNullable(GameData.getTrialAvatarActivityDataByAvatarIndex(trialAvatarIndexId));
    }

    public int getTrialActivityDungeonId(int trialAvatarIndexId) {
        return getActivityData(trialAvatarIndexId).map(TrialAvatarActivityDataData::getDungeonId).orElse(-1);
    }

    public List<String> getTriggerParamList() {
        return getActivityData(this.selectedTrialAvatarIndex).map(TrialAvatarActivityDataData::getTriggerConfig)
            .map(ActivityWatcherData.WatcherTrigger::getParamList).orElse(List.of());
    }

    public boolean enterTrialDungeon(@NonNull Player player, int trialAvatarIndexId, int enterPointId) {
        // TODO, not sure if this will cause problem in MP, since we are entering trial activity dungeon
        player.sendPacket(new PacketScenePlayerLocationNotify(player.getScene())); // official does send this

        if (!player.getServer().getDungeonSystem().enterDungeon(
            player,
            enterPointId,
            getTrialActivityDungeonId(trialAvatarIndexId),
            TRIAL_AVATAR_DUNGEON_SETTLE_LISTENER)) return false;

        this.selectedTrialAvatarIndex = trialAvatarIndexId;
        return true;
    }

    public List<Integer> getBattleAvatarsList() {
        return getActivityData(this.selectedTrialAvatarIndex).map(TrialAvatarActivityDataData::getBattleAvatarsList)
            .filter(avatarStr -> !avatarStr.isBlank()).map(avatarStr -> avatarStr.split(",")).stream()
            .flatMap(Arrays::stream).map(Integer::parseInt).toList();
    }

    public boolean getReward(@NonNull Player player, int trialAvatarIndexId) {
        val playerActivityData = player.getActivityManager()
            .getPlayerActivityDataByActivityType(ActivityType.NEW_ACTIVITY_TRIAL_AVATAR);

        if(playerActivityData.isEmpty()) return false;

        val trialAvatarPlayerData = getTrialAvatarPlayerData(playerActivityData.get());
        val rewardInfo = trialAvatarPlayerData.getRewardInfo(trialAvatarIndexId);
        if (rewardInfo == null) return false;

        val rewardParam = GameData.getRewardDataMap().get(rewardInfo.getRewardId());
        if (rewardParam == null) return false;

        player.getInventory().addItemParamDatas(rewardParam.getRewardItemList(), ActionReason.TrialAvatarActivityFirstPassReward);
        rewardInfo.setReceivedReward(true);
        playerActivityData.get().setDetail(trialAvatarPlayerData);
        playerActivityData.get().save();
        return true;
    }

    public void setPassDungeon(PlayerActivityData playerActivityData) {
        val trialAvatarPlayerData = getTrialAvatarPlayerData(playerActivityData);
        val rewardInfo = trialAvatarPlayerData.getRewardInfo(this.selectedTrialAvatarIndex);
        if (rewardInfo == null) return;

        rewardInfo.setPassedDungeon(true);
        playerActivityData.setDetail(trialAvatarPlayerData);
        playerActivityData.save();
        val player = Grasscutter.getGameServer().getPlayerByUid(playerActivityData.getUid());
        player.sendPacket(new PacketActivityInfoNotify(
            toProto(playerActivityData, player.getActivityManager().getConditionExecutor())
        ));
    }
}
