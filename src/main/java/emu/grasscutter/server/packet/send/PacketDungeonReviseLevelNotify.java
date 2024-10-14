package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.excels.DungeonData;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.DungeonReviseLevelNotify;

public class PacketDungeonReviseLevelNotify extends BaseTypedPacket<DungeonReviseLevelNotify> {
    public PacketDungeonReviseLevelNotify(DungeonData dungeonData) {
        super(new DungeonReviseLevelNotify());
        proto.setDungeonId(dungeonData.getId());
        proto.setReviseLevel(dungeonData.getLevelRevise());
    }
}
