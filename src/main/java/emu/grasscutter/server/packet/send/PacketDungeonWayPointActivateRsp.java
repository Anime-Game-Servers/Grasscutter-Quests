package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.progression.DungeonWayPointActivateRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketDungeonWayPointActivateRsp extends BaseTypedPacket<DungeonWayPointActivateRsp> {
	public PacketDungeonWayPointActivateRsp(boolean success, int pointId) {
        super(new DungeonWayPointActivateRsp());
        proto.setWayPointId(pointId);
        proto.setRetcode(success ? Retcode.RET_SUCC : Retcode.RET_FAIL);
	}
}
