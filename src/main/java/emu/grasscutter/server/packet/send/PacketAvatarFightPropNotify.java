package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.properties.AvatarFightPropNotify;

public class PacketAvatarFightPropNotify extends BaseTypedPacket<AvatarFightPropNotify> {
	public PacketAvatarFightPropNotify(Avatar avatar) {
        super(new AvatarFightPropNotify());
        proto.setAvatarGuid(avatar.getGuid());
        proto.setFightPropMap(avatar.getFightProperties());
	}
}
