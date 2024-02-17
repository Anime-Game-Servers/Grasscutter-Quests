package emu.grasscutter.game.activity.condition.all;

import emu.grasscutter.game.activity.ActivityConfigItem;
import emu.grasscutter.game.activity.PlayerActivityData;
import emu.grasscutter.game.activity.condition.ActivityConditionHandler;
import emu.grasscutter.game.activity.condition.ActivityConditionBaseHandler;

import java.util.List;

import static org.anime_game_servers.game_data_models.data.activity.ActivityCondition.NEW_ACTIVITY_COND_DAYS_LESS;


@ActivityConditionHandler(NEW_ACTIVITY_COND_DAYS_LESS)
public class DayLess extends ActivityConditionBaseHandler {
    @Override
    public boolean execute(PlayerActivityData activityData, ActivityConfigItem activityConfig, List<Integer> params) {
        return true; //TODO implement this and add possibility to always return true
    }
}
