package emu.grasscutter.game.quest.conditions;

import emu.grasscutter.data.excels.QuestData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueCond;

import static emu.grasscutter.game.quest.enums.QuestCond.QUEST_COND_ACTIVITY_OPEN;

@QuestValueCond(QUEST_COND_ACTIVITY_OPEN)
public class ConditionActivityOpen extends BaseCondition {

    @Override
    public boolean execute(Player owner, QuestData questData, QuestData.QuestAcceptCondition condition, String paramStr, int... params) {
        return owner.getActivityManager().isActivityActive(condition.getParam()[0]);
    }

}
