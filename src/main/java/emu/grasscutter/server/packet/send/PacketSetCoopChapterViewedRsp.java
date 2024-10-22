package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.coop.SetCoopChapterViewedRsp;

public class PacketSetCoopChapterViewedRsp extends BaseTypedPacket<SetCoopChapterViewedRsp> {

	public PacketSetCoopChapterViewedRsp(int chapterId) {
		super(new SetCoopChapterViewedRsp(chapterId));
	}
}