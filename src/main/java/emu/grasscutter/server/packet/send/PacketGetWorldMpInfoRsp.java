package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.world.World;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.GetWorldMpInfoRsp;

public class PacketGetWorldMpInfoRsp extends BaseTypedPacket<GetWorldMpInfoRsp> {
	public PacketGetWorldMpInfoRsp(World world) {
        super(new GetWorldMpInfoRsp());
        proto.setInMpMode(world.isMultiplayer());
	}
}
