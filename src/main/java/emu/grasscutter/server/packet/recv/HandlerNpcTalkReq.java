package emu.grasscutter.server.packet.recv;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.common.quest.MainQuestData.TalkData;
import emu.grasscutter.game.quest.enums.QuestCond;
import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketNpcTalkRsp;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.npc.NpcTalkReq;

public class HandlerNpcTalkReq extends TypedPacketHandler<NpcTalkReq> {
    @Override
    public void handle(GameSession session, byte[] header, NpcTalkReq req) throws Exception {

        //Check if mainQuest exists
        //remove last 2 digits to get a mainQuestId
        int talkId = req.getTalkId();
        int mainQuestId = GameData.getQuestTalkMap().getOrDefault(talkId, talkId / 100);
        val mainQuestData = GameData.getMainQuestDataMap().get(mainQuestId);
        val questManager = session.getPlayer().getQuestManager();

        if (mainQuestData != null) {
            // This talk is associated with a quest. Handle it.
            // If the quest has no talk data defined on it, create one.
            var talkForQuest = new TalkData(talkId, "");
            if (mainQuestData.getTalks() != null) {
                val talks = mainQuestData.getTalks().stream().filter(p -> p.getId() == talkId).toList();

                if (talks.size() > 0) {
                    talkForQuest = talks.get(0);
                }
            }

            // Add to the list of done talks for this quest.
            val mainQuest = questManager.getMainQuestByTalkId(talkId);
            if (mainQuest != null) {
                mainQuest.getTalks().put(talkId, talkForQuest);
            }

        }
        // Fire quest triggers either way, since a quest might depend on a talk not mapped to a quest.
        questManager.queueEvent(QuestContent.QUEST_CONTENT_COMPLETE_ANY_TALK, talkId, 0, 0);
        questManager.queueEvent(QuestContent.QUEST_CONTENT_COMPLETE_TALK, talkId, 0);
        questManager.queueEvent(QuestCond.QUEST_COND_COMPLETE_TALK, talkId, 0);

        session.send(new PacketNpcTalkRsp(req.getNpcEntityId(), req.getTalkId(), req.getEntityId()));
    }
}
