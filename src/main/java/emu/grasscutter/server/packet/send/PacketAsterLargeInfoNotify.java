package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.aster.AsterLargeDetailInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.aster.AsterLargeInfoNotify;
import org.anime_game_servers.multi_proto.gi.messages.activity.aster.AsterMidDetailInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.aster.AsterMidInfoNotify;

public class PacketAsterLargeInfoNotify extends BaseTypedPacket<AsterLargeInfoNotify> {

	public PacketAsterLargeInfoNotify(AsterLargeDetailInfo activityInfo) {
		super(new AsterLargeInfoNotify(activityInfo));
	}
}
