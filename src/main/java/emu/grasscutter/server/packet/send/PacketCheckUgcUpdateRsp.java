package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.CheckUgcUpdateRsp;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.UgcType;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketCheckUgcUpdateRsp extends BaseTypedPacket<CheckUgcUpdateRsp> {

	public PacketCheckUgcUpdateRsp(UgcType ugcType) {
		super(new CheckUgcUpdateRsp(Retcode.RET_SUCC, ugcType));
	}
}
