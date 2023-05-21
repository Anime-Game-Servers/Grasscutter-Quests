package emu.grasscutter.game.quest.conditions;

import emu.grasscutter.data.binout.quest.SubQuestData;
import emu.grasscutter.data.excels.QuestData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.QuestValueCond;
import lombok.val;

import static emu.grasscutter.game.quest.enums.QuestCond.QUEST_COND_LUA_NOTIFY;

@QuestValueCond(QUEST_COND_LUA_NOTIFY)
public class ConditionLuaNotify extends BaseCondition {

    @Override
    public boolean execute(Player owner, SubQuestData questData, QuestData.QuestAcceptCondition condition, String paramStr, int... params) {
        val triggerId = Integer.parseInt(paramStr);
        val targetTrigger = condition.getParam()[0];
        return targetTrigger == triggerId;
    }

}
