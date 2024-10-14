package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.ability.ClientAbilityChangeNotify;

public class HandlerClientAbilityChangeNotify extends TypedPacketHandler<ClientAbilityChangeNotify> {

	@Override
	public void handle(GameSession session, byte[] header, ClientAbilityChangeNotify notif) throws Exception {
		Player player = session.getPlayer();
		for (val entry : notif.getInvokes()) {
			player.getAbilityManager().onAbilityInvoke(entry);
			player.getAbilityInvokeHandler().addEntry(entry.getForwardType(), entry);
		}
	}

}
