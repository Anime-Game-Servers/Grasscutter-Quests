package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.variable.QuestUpdateQuestVarNotify;

import java.util.stream.IntStream;

public class PacketQuestUpdateQuestVarNotify extends BaseTypedPacket<QuestUpdateQuestVarNotify> {

    public PacketQuestUpdateQuestVarNotify(int mainQuestId, int... questVars) {
        super(new QuestUpdateQuestVarNotify());
        proto.setParentQuestId(mainQuestId);
        proto.setQuestVar(IntStream.of(questVars).boxed().toList());
    }
}
