package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.world.World;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.WorldPlayerLocationNotify;

public class PacketWorldPlayerLocationNotify extends BaseTypedPacket<WorldPlayerLocationNotify> {

	public PacketWorldPlayerLocationNotify(World world) {
		super(new WorldPlayerLocationNotify());

        proto.setPlayerWorldLocList(world.getPlayers().stream().map(Player::getWorldPlayerLocationInfo).toList());
	}
}
