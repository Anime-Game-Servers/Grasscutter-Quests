package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.battle.event.EvtAiSyncCombatThreatInfoNotify;

public class HandlerEvtAiSyncCombatThreatInfoNotify extends TypedPacketHandler<EvtAiSyncCombatThreatInfoNotify> {

	@Override
    public void handle(GameSession session, byte[] header, EvtAiSyncCombatThreatInfoNotify req) throws Exception {
		// Auto template
	}

}
