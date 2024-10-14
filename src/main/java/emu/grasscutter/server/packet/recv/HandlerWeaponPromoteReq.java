package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.item.upgrade.WeaponPromoteReq;

public class HandlerWeaponPromoteReq extends TypedPacketHandler<WeaponPromoteReq> {
    @Override
    public void handle(GameSession session, byte[] header, WeaponPromoteReq req) throws Exception {
        // Ascend weapon
        session.getServer().getInventorySystem().promoteWeapon(session.getPlayer(), req.getTargetWeaponGuid());
    }
}
