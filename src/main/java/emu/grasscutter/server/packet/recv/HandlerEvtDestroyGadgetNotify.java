package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.battle.event.EvtDestroyGadgetNotify;

public class HandlerEvtDestroyGadgetNotify extends TypedPacketHandler<EvtDestroyGadgetNotify> {

	@Override
	public void handle(GameSession session, byte[] header, EvtDestroyGadgetNotify notify) throws Exception {
		session.getPlayer().getScene().onPlayerDestroyGadget(notify.getEntityId());
	}

}
