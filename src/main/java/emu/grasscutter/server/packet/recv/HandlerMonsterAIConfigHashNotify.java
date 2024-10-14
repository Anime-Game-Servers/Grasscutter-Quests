package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.MonsterAIConfigHashNotify;

public class HandlerMonsterAIConfigHashNotify extends TypedPacketHandler<MonsterAIConfigHashNotify> {
	@Override
    public void handle(GameSession session, byte[] header, MonsterAIConfigHashNotify req) throws Exception {
		// Auto template
	}
}
