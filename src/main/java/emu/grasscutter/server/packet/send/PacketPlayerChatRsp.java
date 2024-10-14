package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.community.chat.PlayerChatRsp;

public class PacketPlayerChatRsp extends BaseTypedPacket<PlayerChatRsp> {

	public PacketPlayerChatRsp() {
		super(new PlayerChatRsp());
	}
}
