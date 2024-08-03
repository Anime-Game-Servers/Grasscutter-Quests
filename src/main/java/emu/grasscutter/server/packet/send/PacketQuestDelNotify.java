package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.quest.QuestDelNotify;

public class PacketQuestDelNotify extends BaseTypedPacket<QuestDelNotify> {

    public PacketQuestDelNotify(int questId) {
        super(new QuestDelNotify(questId));
    }
}