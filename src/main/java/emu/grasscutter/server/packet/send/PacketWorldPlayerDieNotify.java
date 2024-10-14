package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.PlayerDieType;
import org.anime_game_servers.multi_proto.gi.messages.scene.WorldPlayerDieNotify;

public class PacketWorldPlayerDieNotify extends BaseTypedPacket<WorldPlayerDieNotify> {

	public PacketWorldPlayerDieNotify(PlayerDieType playerDieType, int killerId) {
        super(new WorldPlayerDieNotify());
        proto.setDieType(playerDieType);
        WorldPlayerDieNotify.Entity.MonsterId monsterId = new WorldPlayerDieNotify.Entity.MonsterId(killerId);
        proto.setEntity(monsterId);
	}
}
