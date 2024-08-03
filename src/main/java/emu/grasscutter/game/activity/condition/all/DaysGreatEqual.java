package emu.grasscutter.game.activity.condition.all;

import emu.grasscutter.game.activity.ActivityConfigItem;
import emu.grasscutter.game.activity.PlayerActivityData;
import emu.grasscutter.game.activity.condition.ActivityConditionHandler;
import emu.grasscutter.game.activity.condition.ActivityConditionBaseHandler;
import static org.anime_game_servers.game_data_models.data.activity.ActivityCondition.NEW_ACTIVITY_COND_DAYS_GREAT_EQUAL;

import java.util.Date;
import java.util.List;

@ActivityConditionHandler(NEW_ACTIVITY_COND_DAYS_GREAT_EQUAL)
public class DaysGreatEqual extends ActivityConditionBaseHandler {
    @Override
    public boolean execute(PlayerActivityData activityData, ActivityConfigItem activityConfig, List<Integer> params) {
        Date activityBeginTime = activityConfig.getBeginTime();
        long timeDiff = System.currentTimeMillis() - activityBeginTime.getTime();
        int days = (int) (timeDiff / (1000 * 60 * 60 * 24L));
        return days + 1 >= params.get(0);
    }
}
