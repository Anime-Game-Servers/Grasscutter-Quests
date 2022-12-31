package emu.grasscutter.game.quest.content;

import emu.grasscutter.data.excels.QuestData;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueContent;

import static emu.grasscutter.game.quest.enums.QuestContent.QUEST_CONTENT_LEAVE_SCENE;

@QuestValueContent(QUEST_CONTENT_LEAVE_SCENE)
public class ContentLeaveScene extends BaseContent {

    @Override
    public boolean execute(GameQuest quest, QuestData.QuestContentCondition condition, String paramStr, int... params) {
        return quest.getOwner().getScene().getPrevScene() == condition.getParam()[0];
    }

}
