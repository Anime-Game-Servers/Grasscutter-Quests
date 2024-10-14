package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.ability.ClientAbilitiesInitFinishCombineNotify;

public class HandlerClientAbilitiesInitFinishCombineNotify extends TypedPacketHandler<ClientAbilitiesInitFinishCombineNotify> {

    @Override
    public void handle(GameSession session, byte[] header, ClientAbilitiesInitFinishCombineNotify notif) throws Exception {
        Player player = session.getPlayer();

        // Call skill end in the player's ability manager.
        player.getAbilityManager().onSkillEnd(player);

        for(val entry : notif.getEntityInvokeList()) {
            for (val ability : entry.getInvokes()) {
                player.getAbilityManager().onAbilityInvoke(ability);
                player.getClientAbilityInitFinishHandler().addEntry(ability.getForwardType(), ability);
            }

            if (entry.getInvokes().size() > 0) {
                session.getPlayer().getClientAbilityInitFinishHandler().update(session.getPlayer());
            }
        }
    }
}
