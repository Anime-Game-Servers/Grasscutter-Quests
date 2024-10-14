package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.cosmetic.AvatarGainFlycloakNotify;

public class PacketAvatarGainFlycloakNotify extends BaseTypedPacket<AvatarGainFlycloakNotify> {
	public PacketAvatarGainFlycloakNotify(int flycloak) {
        super(new AvatarGainFlycloakNotify());
        proto.setFlycloakId(flycloak);
	}
}
