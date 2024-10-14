package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.child.QuestListUpdateNotify;

import java.util.List;

public class PacketQuestListUpdateNotify extends BaseTypedPacket<QuestListUpdateNotify> {

    public PacketQuestListUpdateNotify(GameQuest quest) {
        this(List.of(quest));
    }

    public PacketQuestListUpdateNotify(List<GameQuest> quests) {
        super(new QuestListUpdateNotify());
        proto.setQuestList(quests.stream().map(GameQuest::toProto).toList());
    }
}
