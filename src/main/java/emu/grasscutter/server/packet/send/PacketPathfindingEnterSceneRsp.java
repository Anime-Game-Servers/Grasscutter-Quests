package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.pathfinding.PathfindingEnterSceneRsp;

public class PacketPathfindingEnterSceneRsp extends BaseTypedPacket<PathfindingEnterSceneRsp> {
	public PacketPathfindingEnterSceneRsp(int clientSequence) {
        super(new PathfindingEnterSceneRsp(), clientSequence);
	}
}
