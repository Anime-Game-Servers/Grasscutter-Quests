package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.ServerCondMeetQuestListUpdateNotify;

import java.util.List;

public class PacketServerCondMeetQuestListUpdateNotify extends BaseTypedPacket<ServerCondMeetQuestListUpdateNotify> {

    public PacketServerCondMeetQuestListUpdateNotify(List<GameQuest> addQuests, List<GameQuest> delQuests) {
        super(new ServerCondMeetQuestListUpdateNotify());
        proto.setAddQuestIdList(addQuests.stream().map(GameQuest::getSubQuestId).toList());
        proto.setDelQuestIdList(delQuests.stream().map(GameQuest::getSubQuestId).toList());
    }

    public PacketServerCondMeetQuestListUpdateNotify() {
        super(new ServerCondMeetQuestListUpdateNotify());
    }
}
