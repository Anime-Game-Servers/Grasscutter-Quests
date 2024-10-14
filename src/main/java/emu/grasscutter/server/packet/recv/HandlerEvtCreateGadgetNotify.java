package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.entity.EntitySolarIsotomaClientGadget;
import emu.grasscutter.game.entity.EntityClientGadget;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.battle.event.EvtCreateGadgetNotify;

public class HandlerEvtCreateGadgetNotify extends TypedPacketHandler<EvtCreateGadgetNotify> {

    @Override
    public void handle(GameSession session, byte[] header, EvtCreateGadgetNotify notify) throws Exception {

        // Sanity check - dont add duplicate entities
        if (session.getPlayer().getScene().getEntityById(notify.getEntityId()) != null) {
            return;
        }

        // Create entity and summon in world
        var gadgetId = notify.getConfigId();
        EntityClientGadget gadget = switch (gadgetId) {
            //Solar Isotoma.
            case EntitySolarIsotomaClientGadget.GADGET_ID ->
                new EntitySolarIsotomaClientGadget(session.getPlayer().getScene(), session.getPlayer(), notify);

            //Default.
            default ->
                new EntityClientGadget(session.getPlayer().getScene(), session.getPlayer(), notify);
        };

        session.getPlayer().getScene().onPlayerCreateGadget(gadget);
    }

}
