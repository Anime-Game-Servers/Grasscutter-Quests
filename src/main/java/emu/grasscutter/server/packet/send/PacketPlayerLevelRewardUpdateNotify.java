package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.player.PlayerLevelRewardUpdateNotify;

import java.util.Set;

public class PacketPlayerLevelRewardUpdateNotify extends BaseTypedPacket<PlayerLevelRewardUpdateNotify> {
	public PacketPlayerLevelRewardUpdateNotify(Set<Integer> rewardedLevels) {
        super(new PlayerLevelRewardUpdateNotify());
        proto.setLevelList(rewardedLevels.stream().toList());
	}
}
