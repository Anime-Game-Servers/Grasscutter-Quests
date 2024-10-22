package emu.grasscutter.game.quest.exec;

import emu.grasscutter.data.common.quest.SubQuestData.QuestExecParam;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueExec;
import emu.grasscutter.game.quest.handlers.QuestExecHandler;
import lombok.val;

import static emu.grasscutter.game.quest.enums.QuestExec.QUEST_EXEC_CHANGE_SCENE_LEVEL_TAG;

@QuestValueExec(QUEST_EXEC_CHANGE_SCENE_LEVEL_TAG)
public class ExecChangeSceneLevelTag extends QuestExecHandler {
    @Override
    public boolean execute(GameQuest quest, QuestExecParam condition, String... paramStr) {
        val levelTagId = Integer.parseInt(paramStr[0]);
        quest.getOwner().setLevelTag(levelTagId);
        return true;
    }
}
