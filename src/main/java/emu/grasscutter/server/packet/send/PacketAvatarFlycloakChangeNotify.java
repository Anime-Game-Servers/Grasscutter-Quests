package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.cosmetic.AvatarFlycloakChangeNotify;

public class PacketAvatarFlycloakChangeNotify extends BaseTypedPacket<AvatarFlycloakChangeNotify> {
	public PacketAvatarFlycloakChangeNotify(Avatar avatar) {
        super(new AvatarFlycloakChangeNotify());
        proto.setAvatarGuid(avatar.getGuid());
        proto.setFlycloakId(avatar.getFlyCloak());
	}
}
