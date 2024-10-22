package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.coop.MainCoop;
import messages.coop.StartCoopPointRsp;

public class PacketStartCoopPointRsp extends BaseTypedPacket<StartCoopPointRsp> {

	public PacketStartCoopPointRsp(int coopPoint, MainCoop startCoop) {
		super(new StartCoopPointRsp(coopPoint));
		proto.setStart(true);
		proto.setStartMainCoop(startCoop);
	}
}