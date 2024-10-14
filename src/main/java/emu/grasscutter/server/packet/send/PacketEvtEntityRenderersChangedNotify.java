package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.battle.event.EvtEntityRenderersChangedNotify;

public class PacketEvtEntityRenderersChangedNotify extends BaseTypedPacket<EvtEntityRenderersChangedNotify> {

	public PacketEvtEntityRenderersChangedNotify(EvtEntityRenderersChangedNotify req) {
		super(req, true);
	}
}
