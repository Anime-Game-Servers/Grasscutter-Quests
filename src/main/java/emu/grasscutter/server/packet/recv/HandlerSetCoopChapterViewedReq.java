package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSetCoopChapterViewedRsp;
import messages.coop.SetCoopChapterViewedReq;

public class HandlerSetCoopChapterViewedReq extends TypedPacketHandler<SetCoopChapterViewedReq> {

	@Override
	public void handle(GameSession session, byte[] header, SetCoopChapterViewedReq req) throws Exception {
		session.getPlayer().getCoopHandler().getCoopCards()
				.get(req.getChapterId())
				.setViewed(true);
		session.send(new PacketSetCoopChapterViewedRsp(req.getChapterId()));
	}

}