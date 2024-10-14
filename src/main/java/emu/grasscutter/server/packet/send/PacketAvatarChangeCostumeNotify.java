package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.AvatarChangeCostumeNotify;

public class PacketAvatarChangeCostumeNotify extends BaseTypedPacket<AvatarChangeCostumeNotify> {

	public PacketAvatarChangeCostumeNotify(EntityAvatar entity) {
		super(new AvatarChangeCostumeNotify(entity.toProto()));
	}
}
