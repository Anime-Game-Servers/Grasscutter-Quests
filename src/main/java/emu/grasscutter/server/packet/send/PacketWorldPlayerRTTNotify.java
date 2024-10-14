package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.world.World;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.other.PlayerRTTInfo;
import org.anime_game_servers.multi_proto.gi.messages.other.WorldPlayerRTTNotify;

public class PacketWorldPlayerRTTNotify extends BaseTypedPacket<WorldPlayerRTTNotify> {
	public PacketWorldPlayerRTTNotify(World world) {
        super(new WorldPlayerRTTNotify());
        proto.setPlayerRttList(world.getPlayers().stream()
            .map(player -> {
                val playerRTTInfo = new PlayerRTTInfo();
                playerRTTInfo.setUid(player.getUid());
                playerRTTInfo.setRtt(10); // TODO - put player ping here
                return playerRTTInfo;
            }).toList());
	}
}
