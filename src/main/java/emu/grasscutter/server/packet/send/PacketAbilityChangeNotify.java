package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.ability.AbilityChangeNotify;

public class PacketAbilityChangeNotify extends BaseTypedPacket<AbilityChangeNotify> {

	public PacketAbilityChangeNotify(EntityAvatar entity) {
		super(new AbilityChangeNotify(entity.getId(), entity.getAbilityControlBlock()), true);
	}
}
