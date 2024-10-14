package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.progression.DungeonRestartRsp;

public class PacketDungeonRestartRsp extends BaseTypedPacket<DungeonRestartRsp> {
    public PacketDungeonRestartRsp() {
        super(new DungeonRestartRsp());
    }
}
