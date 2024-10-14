package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.aster.AsterMidCampInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.aster.AsterMidCampInfoNotify;

import java.util.List;

public class PacketAsterMidCampInfoNotify extends BaseTypedPacket<AsterMidCampInfoNotify> {

	public PacketAsterMidCampInfoNotify(AsterMidCampInfo activityInfo) {
		super(new AsterMidCampInfoNotify(List.of(activityInfo)));
	}
}
