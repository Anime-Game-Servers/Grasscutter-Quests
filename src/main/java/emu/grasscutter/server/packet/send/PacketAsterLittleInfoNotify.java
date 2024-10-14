package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.aster.AsterLittleDetailInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.aster.AsterLittleInfoNotify;
import org.anime_game_servers.multi_proto.gi.messages.activity.aster.AsterMidDetailInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.aster.AsterMidInfoNotify;

public class PacketAsterLittleInfoNotify extends BaseTypedPacket<AsterLittleInfoNotify> {

	public PacketAsterLittleInfoNotify(AsterLittleDetailInfo activityInfo) {
		super(new AsterLittleInfoNotify(activityInfo));
	}
}
