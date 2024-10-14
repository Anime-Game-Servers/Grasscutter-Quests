package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.variable.QuestGlobalVar;
import org.anime_game_servers.multi_proto.gi.messages.quest.variable.QuestGlobalVarNotify;

public class PacketQuestGlobalVarNotify extends BaseTypedPacket<QuestGlobalVarNotify> {
    public PacketQuestGlobalVarNotify(Player player) {
        super(new QuestGlobalVarNotify(player.getQuestGlobalVariables().entrySet().stream()
            .map(entry -> new QuestGlobalVar(entry.getKey(),entry.getValue())).toList()));
    }
}
