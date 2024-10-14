package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.other.ServerTimeNotify;

public class PacketServerTimeNotify extends BaseTypedPacket<ServerTimeNotify> {

	public PacketServerTimeNotify() {
		super(new ServerTimeNotify(System.currentTimeMillis()));
	}
}
