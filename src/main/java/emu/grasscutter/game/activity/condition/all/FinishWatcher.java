package emu.grasscutter.game.activity.condition.all;

import emu.grasscutter.game.activity.ActivityConfigItem;
import emu.grasscutter.game.activity.PlayerActivityData;
import emu.grasscutter.game.activity.condition.ActivityConditionHandler;
import emu.grasscutter.game.activity.condition.ActivityConditionBaseHandler;
import lombok.val;

import java.util.List;

import static org.anime_game_servers.game_data_models.data.activity.ActivityCondition.NEW_ACTIVITY_COND_FINISH_WATCHER;

@ActivityConditionHandler(NEW_ACTIVITY_COND_FINISH_WATCHER)
public class FinishWatcher extends ActivityConditionBaseHandler {

    @Override
    public boolean execute(PlayerActivityData activityData, ActivityConfigItem activityConfig, List<Integer> params) {
        val watcherMap = activityData.getWatcherInfoMap();
        for (int param : params) {
            val watcher = watcherMap.get(param);
            if(watcher == null || !watcher.isFinished()){
                return false;
            }
        }
        return true;
    }
}
