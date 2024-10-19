package emu.grasscutter.game.activity.condition.all;

import emu.grasscutter.game.activity.ActivityConfigItem;
import emu.grasscutter.game.activity.PlayerActivityData;
import emu.grasscutter.game.activity.condition.ActivityConditionHandler;
import emu.grasscutter.game.activity.condition.ActivityConditionBaseHandler;
import emu.grasscutter.game.quest.GameQuest;
import org.anime_game_servers.core.gi.enums.QuestState;

import java.util.List;

import static org.anime_game_servers.game_data_models.data.activity.ActivityCondition.NEW_ACTIVITY_COND_QUEST_FINISH;

@ActivityConditionHandler(NEW_ACTIVITY_COND_QUEST_FINISH)
public class QuestFinished extends ActivityConditionBaseHandler {
    @Override
    public boolean execute(PlayerActivityData activityData, ActivityConfigItem activityConfig, List<Integer> params) {
        GameQuest quest = activityData
            .getPlayer()
            .getQuestManager()
            .getQuestById(params.get(0));

        return quest != null && quest.getState() == QuestState.QUEST_STATE_FINISHED;
    }
}
