package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.child.QuestProgressUpdateNotify;

import java.util.Arrays;

public class PacketQuestProgressUpdateNotify extends BaseTypedPacket<QuestProgressUpdateNotify> {

    public PacketQuestProgressUpdateNotify(GameQuest quest) {
        super(new QuestProgressUpdateNotify());
        proto.setQuestId(quest.getSubQuestId());

        if (quest.getFinishProgressList() != null) {
            proto.setFinishProgressList(Arrays.stream(quest.getFinishProgressList()).boxed().toList());
        }

        if (quest.getFailProgressList() != null) {
            proto.setFailProgressList(Arrays.stream(quest.getFailProgressList()).boxed().toList());
        }
    }
}
