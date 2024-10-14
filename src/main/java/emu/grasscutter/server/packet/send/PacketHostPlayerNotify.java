package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.world.World;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.world.HostPlayerNotify;

public class PacketHostPlayerNotify extends BaseTypedPacket<HostPlayerNotify> {
	public PacketHostPlayerNotify(World world) {
        super(new HostPlayerNotify());
        proto.setHostUid(world.getHost().getUid());
        proto.setHostPeerId(world.getHost().getPeerId());
	}
}
