package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.ability.ClientAbilityInitFinishNotify;

public class HandlerClientAbilityInitFinishNotify extends TypedPacketHandler<ClientAbilityInitFinishNotify> {

    @Override
    public void handle(GameSession session, byte[] header, ClientAbilityInitFinishNotify notif) throws Exception {
        Player player = session.getPlayer();

        // Call skill end in the player's ability manager.
        player.getAbilityManager().onSkillEnd(player);

        for (val entry : notif.getInvokes()) {
            player.getAbilityManager().onAbilityInvoke(entry);
            player.getClientAbilityInitFinishHandler().addEntry(entry.getForwardType(), entry);
        }

        if (notif.getInvokes().size() > 0) {
            session.getPlayer().getClientAbilityInitFinishHandler().update(session.getPlayer());
        }
    }
}
