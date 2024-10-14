package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.aster.AsterLargeDetailInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.aster.AsterLargeInfoNotify;
import org.anime_game_servers.multi_proto.gi.messages.activity.aster.AsterProgressDetailInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.aster.AsterProgressInfoNotify;

public class PacketAsterProgressInfoNotify extends BaseTypedPacket<AsterProgressInfoNotify> {

	public PacketAsterProgressInfoNotify(AsterProgressDetailInfo activityInfo) {
		super(new AsterProgressInfoNotify(activityInfo));
	}
}
