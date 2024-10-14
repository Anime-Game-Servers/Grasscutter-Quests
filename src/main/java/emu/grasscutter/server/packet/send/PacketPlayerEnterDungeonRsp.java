package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.PlayerEnterDungeonRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketPlayerEnterDungeonRsp extends BaseTypedPacket<PlayerEnterDungeonRsp> {

	public PacketPlayerEnterDungeonRsp(int pointId, int dungeonId, boolean success) {
        super(new PlayerEnterDungeonRsp());
        proto.setPointId(pointId);
        proto.setDungeonId(dungeonId);
        proto.setRetcode(success ? Retcode.RET_SUCC : Retcode.RET_FAIL);

	}
}
