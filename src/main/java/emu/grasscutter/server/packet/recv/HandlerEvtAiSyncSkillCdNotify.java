package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.battle.event.EvtAiSyncSkillCdNotify;

public class HandlerEvtAiSyncSkillCdNotify extends TypedPacketHandler<EvtAiSyncSkillCdNotify> {
	@Override
    public void handle(GameSession session, byte[] header, EvtAiSyncSkillCdNotify req) throws Exception {
		// Auto template
	}
}
