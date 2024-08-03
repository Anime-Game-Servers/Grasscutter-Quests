package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.coop.FinishMainCoopRsp;

public class PacketFinishMainCoopRsp extends BaseTypedPacket<FinishMainCoopRsp> {

    public PacketFinishMainCoopRsp(int id, int endingSavePointId) {
        super(new FinishMainCoopRsp(endingSavePointId, id));
    }
}