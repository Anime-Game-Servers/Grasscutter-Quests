package emu.grasscutter.game.quest.content;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestContentCondition;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueContent;
import lombok.val;

import static emu.grasscutter.game.quest.enums.QuestContent.QUEST_CONTENT_SCENE_LEVEL_TAG_EQ;

@QuestValueContent(QUEST_CONTENT_SCENE_LEVEL_TAG_EQ)
public class ContentSceneLevelTagEq extends BaseContent {

    @Override
    public int initialCheck(GameQuest quest, SubQuestData questData, QuestContentCondition condition) {
        val levelTagId = condition.getParam()[0];
        return quest.getOwner().getLevelTags().getOrDefault(levelTagId, false) ? 1 : 0;
    }

    @Override
    public boolean checkProgress(GameQuest quest, QuestContentCondition condition, int currentProgress) {
        val levelTagId = condition.getParam()[0];
        return quest.getOwner().getLevelTags().getOrDefault(levelTagId, false);
    }
}
