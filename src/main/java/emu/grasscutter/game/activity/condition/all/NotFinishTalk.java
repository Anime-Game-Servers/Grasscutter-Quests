package emu.grasscutter.game.activity.condition.all;

import emu.grasscutter.game.activity.ActivityConfigItem;
import emu.grasscutter.game.activity.PlayerActivityData;
import emu.grasscutter.game.activity.condition.ActivityConditionHandler;
import emu.grasscutter.game.activity.condition.ActivityConditionBaseHandler;

import java.util.List;

import static org.anime_game_servers.game_data_models.data.activity.ActivityCondition.NEW_ACTIVITY_COND_NOT_FINISH_TALK;

@ActivityConditionHandler(NEW_ACTIVITY_COND_NOT_FINISH_TALK)
public class NotFinishTalk extends ActivityConditionBaseHandler {
    @Override
    public boolean execute(PlayerActivityData activityData, ActivityConfigItem activityConfig, List<Integer> params) {
        return activityData
            .getPlayer()
            .getQuestManager()
            .getMainQuests()
            .int2ObjectEntrySet()
            .stream()
            .noneMatch(q -> q.getValue().getTalks().get(params.get(0)) != null); //FIXME taken from ContentCompleteTalk
    }
}
