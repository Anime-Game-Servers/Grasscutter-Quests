package emu.grasscutter.server.packet.recv;

import emu.grasscutter.data.GameData;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.*;
import lombok.val;
import messages.coop.CancelCoopTaskReq;
import java.util.List;

public class HandlerCancelCoopTaskReq extends TypedPacketHandler<CancelCoopTaskReq> {

	@Override
	public void handle(GameSession session, byte[] header, CancelCoopTaskReq req) throws Exception {

		//send MainCoopUpdateNotify
		val mainCoop = session.getPlayer().getCoopHandler().getCoopCards().get(req.getChapterId()).getMainCoop().toProto();
		session.send(new PacketMainCoopUpdateNotify(List.of(mainCoop)));

		//send quest packets
		val curCoopPoint = session.getPlayer().getCoopHandler().getCurCoopPoint();
		val coopPoint = GameData.getCoopPointDataMap().values().stream().filter(x -> x.getId() == curCoopPoint).toList().get(0);
		val curMainQuest = session.getPlayer().getQuestManager().getQuestById(coopPoint.getAcceptQuest()).getMainQuest();
		curMainQuest.finish(false);
		session.send(new PacketQuestDelNotify(coopPoint.getAcceptQuest()));

		//zero out curCoopPoint
		session.getPlayer().getCoopHandler().setCurCoopPoint(0);

		//CoopProgressUpdateNotify
		session.send(new PacketCoopProgressUpdateNotify(0, false));

		//CancelCoopTaskRsp
		session.send(new PacketCancelCoopTaskRsp(req.getChapterId()));
	}

}