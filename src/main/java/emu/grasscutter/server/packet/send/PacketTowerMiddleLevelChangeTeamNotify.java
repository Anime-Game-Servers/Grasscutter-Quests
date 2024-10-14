package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.run.TowerMiddleLevelChangeTeamNotify;

public class PacketTowerMiddleLevelChangeTeamNotify extends BaseTypedPacket<TowerMiddleLevelChangeTeamNotify> {
	public PacketTowerMiddleLevelChangeTeamNotify() {
        super(new TowerMiddleLevelChangeTeamNotify());
	}
}
