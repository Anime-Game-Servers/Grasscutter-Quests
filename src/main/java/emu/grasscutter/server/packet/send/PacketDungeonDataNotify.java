package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.excels.DungeonData;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.DungeonDataNotify;

public class PacketDungeonDataNotify extends BaseTypedPacket<DungeonDataNotify> {
    public PacketDungeonDataNotify(DungeonData dungeonData) {
        super(new DungeonDataNotify());
        // TODO
    }
}
