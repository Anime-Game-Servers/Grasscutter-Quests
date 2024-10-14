package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.quest.GameMainQuest;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.parent.FinishedParentQuestUpdateNotify;

import java.util.List;

public class PacketFinishedParentQuestUpdateNotify extends BaseTypedPacket<FinishedParentQuestUpdateNotify> {

    public PacketFinishedParentQuestUpdateNotify(GameMainQuest quest) {
        this(List.of(quest));
    }

    public PacketFinishedParentQuestUpdateNotify(List<GameMainQuest> quests) {
        super(new FinishedParentQuestUpdateNotify());
        proto.setParentQuestList(quests.stream().map(mainQuest -> mainQuest.toProto(true)).toList());
    }
}
