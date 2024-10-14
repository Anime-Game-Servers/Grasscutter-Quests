package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.item.management.SetEquipLockStateReq;

public class HandlerSetEquipLockStateReq extends TypedPacketHandler<SetEquipLockStateReq> {
    @Override
    public void handle(GameSession session, byte[] header, SetEquipLockStateReq req) throws Exception {
        session.getServer().getInventorySystem().lockEquip(session.getPlayer(), req.getTargetEquipGuid(), req.isLocked());
    }
}
