package emu.grasscutter.game.activity.condition.all;

import emu.grasscutter.game.activity.ActivityConfigItem;
import emu.grasscutter.game.activity.PlayerActivityData;
import emu.grasscutter.game.activity.condition.ActivityConditionHandler;
import emu.grasscutter.game.activity.condition.ActivityConditionBaseHandler;

import java.util.List;

import static org.anime_game_servers.game_data_models.data.activity.ActivityCondition.NEW_ACTIVITY_COND_PLAYER_LEVEL_GREAT_EQUAL;

@ActivityConditionHandler(NEW_ACTIVITY_COND_PLAYER_LEVEL_GREAT_EQUAL)
public class PlayerLevelGreatEqualActivityActivityCondition extends ActivityConditionBaseHandler {

    @Override
    public boolean execute(PlayerActivityData activityData, ActivityConfigItem activityConfig, List<Integer> params) {
        return activityData.getPlayer().getLevel() >= params.get(0);
    }
}
