package emu.grasscutter.game.activity.condition;

import emu.grasscutter.game.activity.ActivityConfigItem;
import emu.grasscutter.game.activity.PlayerActivityData;
import org.anime_game_servers.game_data_models.data.activity.ActivityCondData;

import java.util.List;

/**
 * Base handler for all activity conditions that are listed in NewActivityCondExcelConfigData.json ({@link ActivityCondData})
 */
public abstract class ActivityConditionBaseHandler {

    /**
     * Execute activity condition handler and return result of it's calculation
     *
     * @param activityData   {@link PlayerActivityData} object containing info about activity
     * @param activityConfig
     * @param params         params for handler
     * @return result of condition calculation
     */
    public abstract boolean execute(PlayerActivityData activityData, ActivityConfigItem activityConfig, List<Integer> params);
}
