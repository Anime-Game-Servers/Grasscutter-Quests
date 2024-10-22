package emu.grasscutter.game.quest.conditions;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestAcceptCondition;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.player.CoopHandler.CoopCardEntry;
import emu.grasscutter.game.quest.QuestValueCond;
import lombok.val;
import static emu.grasscutter.game.quest.enums.QuestCond.QUEST_COND_MAIN_COOP_ENTER_SAVE_POINT;

@QuestValueCond(QUEST_COND_MAIN_COOP_ENTER_SAVE_POINT)
public class ConditionMainCoopEnterSavePoint extends BaseCondition {

    @Override
    public boolean execute(Player owner, SubQuestData questData, QuestAcceptCondition condition, String paramStr, int... params) {
        val chapterId = condition.getParam()[0];
        val savePointId = condition.getParam()[1];
        return owner.getCoopHandler().getCoopCards().computeIfAbsent(chapterId, v -> new CoopCardEntry(chapterId)).getMainCoop().getSavePointIdList().contains(savePointId);
    }
}