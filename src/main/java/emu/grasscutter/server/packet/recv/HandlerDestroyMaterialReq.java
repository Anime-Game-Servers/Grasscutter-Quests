package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.item.management.DestroyMaterialReq;

public class HandlerDestroyMaterialReq extends TypedPacketHandler<DestroyMaterialReq> {
    @Override
    public void handle(GameSession session, byte[] header, DestroyMaterialReq req) throws Exception {
        // Delete items
        session.getServer().getInventorySystem().destroyMaterial(session.getPlayer(), req.getMaterialList());
    }
}
