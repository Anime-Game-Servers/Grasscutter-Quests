package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.coop.CoopProgressUpdateNotify;

public class PacketCoopProgressUpdateNotify extends BaseTypedPacket<CoopProgressUpdateNotify> {

    public PacketCoopProgressUpdateNotify(int curCoopPoint, boolean isHaveProgress) {
        super(new CoopProgressUpdateNotify(curCoopPoint, isHaveProgress));
    }
}