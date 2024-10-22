package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.coop.SaveMainCoopRsp;
import java.util.List;

public class PacketSaveMainCoopRsp extends BaseTypedPacket<SaveMainCoopRsp> {

    public PacketSaveMainCoopRsp(int id, List<Integer> savePointId) {
        super(new SaveMainCoopRsp(id, 0, savePointId));
    }
}