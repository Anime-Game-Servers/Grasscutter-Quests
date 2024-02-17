package emu.grasscutter.game.activity.condition;

import org.anime_game_servers.game_data_models.data.activity.ActivityCondition;
import org.anime_game_servers.game_data_models.data.activity.ActivityCondData;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * This annotation marks condition types for NewActivityCondExcelConfigData.json ({@link ActivityCondData}). To use it you should mark
 * class that extends ActivityConditionBaseHandler, and it will be automatically picked during activity manager initiation.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityConditionHandler {
    ActivityCondition value();
}
