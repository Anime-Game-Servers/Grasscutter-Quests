package emu.grasscutter.server.packet.recv;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.quest.enums.QuestCond;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketMainCoopUpdateNotify;
import emu.grasscutter.server.packet.send.PacketStartCoopPointRsp;
import lombok.val;
import messages.coop.MainCoop;
import messages.coop.StartCoopPointReq;
import messages.coop.Status;
import java.util.HashMap;
import java.util.List;

public class HandlerStartCoopPointReq extends TypedPacketHandler<StartCoopPointReq> {

	@Override
	public void handle(GameSession session, byte[] header, StartCoopPointReq req) throws Exception {
		val coopPoint = GameData.getCoopPointDataMap().values().stream()
				.filter(x -> x.getId() == req.getCoopPoint())
				.toList();
		if(!coopPoint.isEmpty()) {
            val chapterId = coopPoint.get(0).getChapterId();
            val chapterData = GameData.getCoopChapterDataMap().values().stream()
                    .filter(j -> j.getId() == chapterId)
                    .toList()
                    .get(0);

            //save
            var mCoop = new MainCoop();
            mCoop.setId(chapterId);
            mCoop.setSelfConfidence(chapterData.getConfidenceValue());
            mCoop.setStatus(Status.RUNNING);
            val tempMap = new HashMap<Integer, Integer>();
            //TODO: investigate this tempMap variable
            //It's tempValueMap in BinOutput/Coop/Coop101401
            //IDK what it does.
            //OOOH!! temperament!!
            tempMap.put(3, 3);
            mCoop.setTempVarMap(tempMap);
            session.getPlayer().getCoopHandler().getCoopCards().get(chapterId).getMainCoop().fromProto(mCoop);

            //packets
            session.send(new PacketMainCoopUpdateNotify(List.of(mCoop)));

            //quests
            val acceptQuest = coopPoint.get(0).getAcceptQuest();
            session.getPlayer().getQuestManager().queueEvent(QuestCond.QUEST_COND_MAIN_COOP_START, chapterId, acceptQuest);

            //more packets
            session.send(new PacketStartCoopPointRsp(req.getCoopPoint(), mCoop));
        }
	}

}