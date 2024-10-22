package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.coop.UnlockCoopChapterRsp;

public class PacketUnlockCoopChapterRsp extends BaseTypedPacket<UnlockCoopChapterRsp> {

	public PacketUnlockCoopChapterRsp(int chapterId) {
		super(new UnlockCoopChapterRsp(chapterId));
	}
}