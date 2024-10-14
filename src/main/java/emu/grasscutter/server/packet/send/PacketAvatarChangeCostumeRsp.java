package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.cosmetic.AvatarChangeCostumeRsp;

public class PacketAvatarChangeCostumeRsp extends BaseTypedPacket<AvatarChangeCostumeRsp> {

	public PacketAvatarChangeCostumeRsp(long avatarGuid, int costumeId) {
		super(new AvatarChangeCostumeRsp(Retcode.RET_SUCC, costumeId, avatarGuid));
	}

	public PacketAvatarChangeCostumeRsp() {
		super(new AvatarChangeCostumeRsp(Retcode.RET_SVR_ERROR));

	}
}
