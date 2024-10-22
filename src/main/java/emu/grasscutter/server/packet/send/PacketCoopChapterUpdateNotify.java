package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.coop.CoopChapter;
import messages.coop.CoopChapterUpdateNotify;
import java.util.List;

public class PacketCoopChapterUpdateNotify extends BaseTypedPacket<CoopChapterUpdateNotify> {

    public PacketCoopChapterUpdateNotify(List<CoopChapter> coopChapterList) {
        super(new CoopChapterUpdateNotify(coopChapterList));
    }
}