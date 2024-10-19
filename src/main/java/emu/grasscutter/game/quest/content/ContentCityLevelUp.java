package emu.grasscutter.game.quest.content;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueContent;
import emu.grasscutter.game.quest.enums.QuestContent;
import lombok.val;


@QuestValueContent(QuestContent.QUEST_CONTENT_CITY_LEVEL_UP)
public class ContentCityLevelUp extends BaseContent {
    @Override
    public int initialCheck(GameQuest quest, SubQuestData questData, SubQuestData.QuestContentCondition condition) {
        val cityId = condition.getParam()[0];
        val level = condition.getParam()[1];
        val checkCityData = quest.getOwner().getSotsManager().getCityInfo(cityId);
        if(checkCityData == null){
            return 0;
        }
        return checkCityData.getLevel() >= level ? 1 : 0;
    }

    @Override
    public int updateProgress(GameQuest quest, int currentProgress, SubQuestData.QuestContentCondition condition, String paramStr, int... params) {
        val requiredLevel = condition.getParam()[1];
        val currentLevel = params[1];
        return currentLevel >= requiredLevel ? 1 : 0;
    }
}
