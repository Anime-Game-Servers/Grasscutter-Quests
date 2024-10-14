package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.pathfinding.ObstacleModifyNotify;

public class HandlerObstacleModifyNotify extends TypedPacketHandler<ObstacleModifyNotify> {
	@Override
    public void handle(GameSession session, byte[] header, ObstacleModifyNotify req) throws Exception {
		// Auto template
	}
}
