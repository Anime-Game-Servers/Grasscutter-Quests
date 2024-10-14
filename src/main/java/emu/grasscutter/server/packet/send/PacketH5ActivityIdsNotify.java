package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.web_event.H5ActivityIdsNotify;

public class PacketH5ActivityIdsNotify extends BaseTypedPacket<H5ActivityIdsNotify> {
	public PacketH5ActivityIdsNotify() {
        super(new H5ActivityIdsNotify());
	}
}
