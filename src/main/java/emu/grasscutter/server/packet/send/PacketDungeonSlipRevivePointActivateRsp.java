package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.DungeonSlipRevivePointActivateRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketDungeonSlipRevivePointActivateRsp extends BaseTypedPacket<DungeonSlipRevivePointActivateRsp> {
	public PacketDungeonSlipRevivePointActivateRsp(boolean success, int pointId) {
        super(new DungeonSlipRevivePointActivateRsp());
        proto.setSlipRevivePointId(pointId);
        proto.setRetcode(success ? Retcode.RET_SUCC : Retcode.RET_FAIL);
	}
}
