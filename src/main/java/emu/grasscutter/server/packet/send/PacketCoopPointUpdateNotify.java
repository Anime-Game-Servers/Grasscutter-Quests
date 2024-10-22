package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.coop.*;

public class PacketCoopPointUpdateNotify extends BaseTypedPacket<CoopPointUpdateNotify> {

    public PacketCoopPointUpdateNotify(CoopPoint coopPoint) {
        super(new CoopPointUpdateNotify());
        proto.setCoopPoint(coopPoint);
    }
}