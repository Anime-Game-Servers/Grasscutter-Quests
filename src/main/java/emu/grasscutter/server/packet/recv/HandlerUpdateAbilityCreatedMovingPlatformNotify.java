package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.entity.gadget.platform.AbilityRoute;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.gadget.UpdateAbilityCreatedMovingPlatformNotify;

public class HandlerUpdateAbilityCreatedMovingPlatformNotify extends TypedPacketHandler<UpdateAbilityCreatedMovingPlatformNotify> {
    @Override
    public void handle(GameSession session, byte[] header, UpdateAbilityCreatedMovingPlatformNotify req) throws Exception {
        var entity = session.getPlayer().getScene().getEntityById(req.getEntityId());

        if (!(entity instanceof EntityGadget entityGadget) || !(entityGadget.getRouteConfig() instanceof AbilityRoute)) {
            return;
        }

        switch (req.getOpType()) {
            case OP_ACTIVATE -> entityGadget.startPlatform();
            case OP_DEACTIVATE -> entityGadget.stopPlatform();
        }
    }
}
