package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.ActivityInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.ActivityInfoNotify;

public class PacketActivityInfoNotify extends BaseTypedPacket<ActivityInfoNotify> {

	public PacketActivityInfoNotify(ActivityInfo activityInfo) {
		super(new ActivityInfoNotify(activityInfo));
	}
}
