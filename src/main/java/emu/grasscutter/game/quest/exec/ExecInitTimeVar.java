package emu.grasscutter.game.quest.exec;

import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueExec;
import emu.grasscutter.game.quest.enums.QuestExec;
import emu.grasscutter.game.quest.handlers.QuestExecHandler;
import emu.grasscutter.data.common.quest.SubQuestData.QuestExecParam;
import lombok.val;

@QuestValueExec(QuestExec.QUEST_EXEC_INIT_TIME_VAR)
public class ExecInitTimeVar extends QuestExecHandler {
    @Override
    public boolean execute(GameQuest quest, QuestExecParam condition, String... paramStr) {
        val timeVarId = Integer.parseInt(condition.getParam()[0]);
        val mainQuest = quest.getMainQuest();
        return mainQuest.initTimeVar(timeVarId);
    }
}
