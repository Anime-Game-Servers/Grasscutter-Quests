package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.dungeons.dungeon_results.BaseDungeonResult;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.progression.DungeonSettleNotify;

public class PacketDungeonSettleNotify extends BaseTypedPacket<DungeonSettleNotify> {
    public PacketDungeonSettleNotify(BaseDungeonResult result) {
        super(result.getProto());
    }
}
