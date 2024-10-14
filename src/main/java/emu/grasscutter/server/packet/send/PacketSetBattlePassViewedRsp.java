package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.battle_pass.SetBattlePassViewedRsp;

public class PacketSetBattlePassViewedRsp extends BaseTypedPacket<SetBattlePassViewedRsp> {
	public PacketSetBattlePassViewedRsp(int scheduleId) {
        super(new SetBattlePassViewedRsp());
        proto.setScheduleId(scheduleId);
	}
}
