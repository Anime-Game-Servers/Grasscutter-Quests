package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.CloseCommonTipsNotify;

public class PacketCloseCommonTipsNotify extends BaseTypedPacket<CloseCommonTipsNotify> {
	public PacketCloseCommonTipsNotify() {
        super(new CloseCommonTipsNotify());
	}
}
