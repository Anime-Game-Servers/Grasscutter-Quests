package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.aster.AsterMidDetailInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.aster.AsterMidInfoNotify;

public class PacketAsterMidInfoNotify extends BaseTypedPacket<AsterMidInfoNotify> {

	public PacketAsterMidInfoNotify(AsterMidDetailInfo activityInfo) {
		super(new AsterMidInfoNotify(activityInfo));
	}
}
