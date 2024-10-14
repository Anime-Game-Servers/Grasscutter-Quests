package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.community.player_presentation.UnlockNameCardNotify;

public class PacketUnlockNameCardNotify extends BaseTypedPacket<UnlockNameCardNotify> {

	public PacketUnlockNameCardNotify(int nameCard) {
        super(new UnlockNameCardNotify());
        proto.setNameCardId(nameCard);
	}
}
