package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.CheckUgcStateRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketCheckUgcStateRsp extends BaseTypedPacket<CheckUgcStateRsp> {

	public PacketCheckUgcStateRsp(Retcode ret) {
		super(new CheckUgcStateRsp(ret));
	}

}
