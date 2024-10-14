package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.SyncScenePlayTeamEntityNotify;

public class PacketSyncScenePlayTeamEntityNotify extends BaseTypedPacket<SyncScenePlayTeamEntityNotify> {

	public PacketSyncScenePlayTeamEntityNotify(Player player) {
		super(new SyncScenePlayTeamEntityNotify(player.getSceneId()));
	}
}
