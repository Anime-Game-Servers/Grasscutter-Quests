package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.player.PlayerTimeNotify;

public class PacketPlayerTimeNotify extends BaseTypedPacket<PlayerTimeNotify> {

	public PacketPlayerTimeNotify(Player player) {
        super(new PlayerTimeNotify());
        proto.setPaused(player.isPaused());
        proto.setPlayerTime(player.getClientTime());
        proto.setServerTime(System.currentTimeMillis());
	}
}
