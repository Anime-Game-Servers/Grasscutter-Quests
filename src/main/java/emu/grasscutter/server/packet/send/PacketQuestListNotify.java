package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.child.Quest;
import org.anime_game_servers.multi_proto.gi.messages.quest.child.QuestListNotify;
import org.anime_game_servers.core.gi.enums.QuestState;

import java.util.ArrayList;
import java.util.List;

public class PacketQuestListNotify extends BaseTypedPacket<QuestListNotify> {

    public PacketQuestListNotify(Player player) {
        super(new QuestListNotify(), true);

        List<Quest> questList = new ArrayList<>();
        player.getQuestManager().forEachQuest(quest -> {
            if (quest.getState() != QuestState.QUEST_STATE_UNSTARTED) {
                questList.add(quest.toProto());
            }
        });
        proto.setQuestList(questList);
    }
}
