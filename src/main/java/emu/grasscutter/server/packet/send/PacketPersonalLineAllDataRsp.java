package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.quest.GameMainQuest;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.data.excels.PersonalLineData;
import org.anime_game_servers.multi_proto.gi.messages.quest.personal.PersonalLineAllDataRsp;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class PacketPersonalLineAllDataRsp extends BaseTypedPacket<PersonalLineAllDataRsp> {
    public PacketPersonalLineAllDataRsp(Collection<GameMainQuest> gameMainQuestList) {
        super(new PersonalLineAllDataRsp());
        var questList = gameMainQuestList.stream()
            .map(GameMainQuest::getChildQuests)
            .map(Map::values)
            .flatMap(Collection::stream)
            .map(GameQuest::getSubQuestId)
            .collect(Collectors.toSet());

        proto.setCanBeUnlockedPersonalLineList(GameData.getPersonalLineDataMap().values().stream()
            .filter(i -> !questList.contains(i.getStartQuestId()))
            .map(PersonalLineData::getId)
            .toList());
    }
}
