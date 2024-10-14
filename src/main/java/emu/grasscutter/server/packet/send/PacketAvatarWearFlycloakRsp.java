package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.cosmetic.AvatarWearFlycloakRsp;

public class PacketAvatarWearFlycloakRsp extends BaseTypedPacket<AvatarWearFlycloakRsp> {
	public PacketAvatarWearFlycloakRsp(long avatarGuid, int costumeId) {
        super(new AvatarWearFlycloakRsp());
        proto.setAvatarGuid(avatarGuid);
        proto.setFlycloakId(costumeId);
	}

	public PacketAvatarWearFlycloakRsp() {
        super(new AvatarWearFlycloakRsp());
        proto.setRetcode(Retcode.RET_SVR_ERROR);
	}
}
