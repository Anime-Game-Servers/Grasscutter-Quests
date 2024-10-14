package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.enums.ParentQuestState;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.parent.FinishedParentQuestNotify;

public class PacketFinishedParentQuestNotify extends BaseTypedPacket<FinishedParentQuestNotify> {

    public PacketFinishedParentQuestNotify(Player player) {
        super(new FinishedParentQuestNotify(), true);
        proto.setParentQuestList(player.getQuestManager().getMainQuests().values().stream()
            //Canceled Quests do not appear in this packet
            .filter(mainQuest -> mainQuest.getState() != ParentQuestState.PARENT_QUEST_STATE_CANCELED)
            .map(mainQuest -> mainQuest.toProto(false))
            .toList());
    }
}
