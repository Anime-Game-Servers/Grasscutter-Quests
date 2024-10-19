package emu.grasscutter.game.activity;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.server.ActivityCondGroup;
import emu.grasscutter.game.activity.condition.ActivityConditionExecutor;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.enums.QuestCond;
import emu.grasscutter.utils.DateHelper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.ActivityInfo;
import org.anime_game_servers.game_data_models.data.activity.ActivityData;
import org.anime_game_servers.game_data_models.data.watcher.WatcherTriggerType;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class ActivityHandler {
    /**
     * Must set before initWatchers
     */
    @Getter ActivityConfigItem activityConfigItem;
    @Getter ActivityData activityData;
    Map<WatcherTriggerType, List<ActivityWatcher>> watchersMap = new HashMap<>();


    abstract public void onProtoBuild(PlayerActivityData playerActivityData, ActivityInfo activityInfo);
    abstract public void onInitPlayerActivityData(PlayerActivityData playerActivityData);

    public void initWatchers(Map<WatcherTriggerType, ConstructorAccess<?>> activityWatcherTypeMap){
        activityData = GameData.getActivityDataMap().get(activityConfigItem.getActivityId());

        // add watcher to map by id
        activityData.getWatcherIds().forEach(watcherId -> {
            val watcherData = GameData.getActivityWatcherDataMap().get(watcherId.intValue());
            if(watcherData == null || watcherData.getTriggerConfig() == null || watcherData.getTriggerConfig().getTriggerType() == null){
                // todo log
                return;
            }
            val triggerType = watcherData.getTriggerConfig().getTriggerType();
            var watcherType = activityWatcherTypeMap.get(triggerType);
            ActivityWatcher watcher;
            if(watcherType != null){
                watcher = (ActivityWatcher) watcherType.newInstance();
            }else{
                watcher = new DefaultWatcher();
            }

            watcher.setWatcherId(watcherData.getId());
            watcher.setActivityHandler(this);
            watcher.setActivityWatcherData(watcherData);
            watchersMap.computeIfAbsent(triggerType, k -> new ArrayList<>());
            watchersMap.get(triggerType).add(watcher);
        });
    }

    protected void triggerCondEvents(Player player){
        if(activityData == null){
            return;
        }
        val questManager = player.getQuestManager();
        activityData.getCondGroupIds().forEach(condGroupId -> {
            val condGroup = GameData.getActivityCondGroupMap().get((int)condGroupId);
            if(condGroup != null)
                condGroup.getCondIds().forEach(condID -> questManager.queueEvent(QuestCond.QUEST_COND_ACTIVITY_COND, condID));
        });
    }

    private List<Integer> getActivityConditions(){
        if(activityData == null){
            return new ArrayList<>();
        }
        return activityData.getCondGroupIds().stream().map(condGroupId -> GameData.getActivityCondGroupMap().get((int)condGroupId))
            .filter(Objects::nonNull)
            .map(ActivityCondGroup::getCondIds)
            .flatMap(Collection::stream)
            .toList();
    }

    // TODO handle possible overwrites
    private List<Integer> getMeetConditions(ActivityConditionExecutor conditionExecutor){
        return conditionExecutor.getMeetActivitiesConditions(getActivityConditions());
    }

    private Map<Integer, PlayerActivityData.WatcherInfo> initWatchersDataForPlayer(){
        return watchersMap.values().stream()
            .flatMap(Collection::stream)
            .map(PlayerActivityData.WatcherInfo::init)
            .collect(Collectors.toMap(PlayerActivityData.WatcherInfo::getWatcherId, y -> y));
    }

    public PlayerActivityData initPlayerActivityData(Player player){
        PlayerActivityData playerActivityData = PlayerActivityData.of()
            .activityId(activityConfigItem.getActivityId())
            .uid(player.getUid())
            .watcherInfoMap(initWatchersDataForPlayer())
            .build();

        onInitPlayerActivityData(playerActivityData);
        return playerActivityData;
    }

    public ActivityInfo toProto(PlayerActivityData playerActivityData, ActivityConditionExecutor conditionExecutor){
        val proto = new ActivityInfo();
        proto.setActivityId(activityConfigItem.getActivityId());
        proto.setActivityType(activityConfigItem.getActivityType());
        proto.setScheduleId(activityConfigItem.getScheduleId());
        proto.setBeginTime(DateHelper.getUnixTime(activityConfigItem.getBeginTime()));
        proto.setFirstDayStartTime(DateHelper.getUnixTime(activityConfigItem.getBeginTime()));
        proto.setEndTime(DateHelper.getUnixTime(activityConfigItem.getEndTime()));
        proto.setMeetCondList(getMeetConditions(conditionExecutor));

        if (playerActivityData != null){
            proto.setWatcherInfoList(playerActivityData.getAllWatcherInfoList());
        }

        onProtoBuild(playerActivityData, proto);

        return proto;
    }

}
