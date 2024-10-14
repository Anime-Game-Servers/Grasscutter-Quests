package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.arangement.HomeUpdateArrangementInfoRsp;

public class PacketHomeUpdateArrangementInfoRsp extends BaseTypedPacket<HomeUpdateArrangementInfoRsp> {
	public PacketHomeUpdateArrangementInfoRsp() {
        super(new HomeUpdateArrangementInfoRsp());
	}
}
