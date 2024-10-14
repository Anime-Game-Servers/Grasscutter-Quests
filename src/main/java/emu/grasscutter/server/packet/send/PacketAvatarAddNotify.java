package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.AvatarAddNotify;

public class PacketAvatarAddNotify extends BaseTypedPacket<AvatarAddNotify> {

	public PacketAvatarAddNotify(Avatar avatar, boolean addedToTeam) {
		super(new AvatarAddNotify(avatar.toProto(), addedToTeam));
	}
}
