package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.community.player_presentation.SetNameCardRsp;

public class PacketSetNameCardRsp extends BaseTypedPacket<SetNameCardRsp> {
	public PacketSetNameCardRsp(int nameCardId) {
        super(new SetNameCardRsp());
        proto.setNameCardId(nameCardId);
	}
}
