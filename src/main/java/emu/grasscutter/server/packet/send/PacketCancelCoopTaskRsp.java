package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.coop.CancelCoopTaskRsp;

public class PacketCancelCoopTaskRsp extends BaseTypedPacket<CancelCoopTaskRsp> {

	public PacketCancelCoopTaskRsp(int chapterId) {
		super(new CancelCoopTaskRsp(chapterId));
	}
}