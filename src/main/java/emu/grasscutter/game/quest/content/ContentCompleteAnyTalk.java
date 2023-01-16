package emu.grasscutter.game.quest.content;

import emu.grasscutter.data.excels.QuestData;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueContent;
import lombok.val;

import java.util.Stream;

import static emu.grasscutter.game.quest.enums.QuestContent.QUEST_CONTENT_COMPLETE_ANY_TALK;

@QuestValueContent(QUEST_CONTENT_COMPLETE_ANY_TALK)
public class ContentCompleteAnyTalk extends BaseContent {

    @Override
    public boolean execute(GameQuest quest, QuestData.QuestContentCondition condition, String paramStr, int... params) {
        return Stream.of(condition.getParamStr().split(",")).mapToInt(Integer::parseInt).anyMatch(talkId -> 
            GameData.getTalkConfigDataMap().get(params[0]) != null && talkId == params[0]);
    }

}
