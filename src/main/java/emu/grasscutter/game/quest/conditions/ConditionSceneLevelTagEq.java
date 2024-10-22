package emu.grasscutter.game.quest.conditions;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestAcceptCondition;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.QuestValueCond;
import lombok.val;

import static emu.grasscutter.game.quest.enums.QuestCond.QUEST_COND_SCENE_LEVEL_TAG_EQ;

@QuestValueCond(QUEST_COND_SCENE_LEVEL_TAG_EQ)
public class ConditionSceneLevelTagEq extends BaseCondition {
    @Override
    public boolean execute(Player owner, SubQuestData questData, QuestAcceptCondition condition, String paramStr, int... params) {
        val levelTagId = condition.getParam()[0];
        return owner.getLevelTags().getOrDefault(levelTagId, false);
    }
}
