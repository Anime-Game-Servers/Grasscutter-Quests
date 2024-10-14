package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.progression.DungeonWayPointNotify;

import java.util.Set;

public class PacketDungeonWayPointNotify extends BaseTypedPacket<DungeonWayPointNotify> {
	public PacketDungeonWayPointNotify(boolean added, Set<Integer> activePointIds) {
        super(new DungeonWayPointNotify());
        proto.setActiveWayPointList(activePointIds.stream().toList());
        proto.setAdd(added);
	}
}
