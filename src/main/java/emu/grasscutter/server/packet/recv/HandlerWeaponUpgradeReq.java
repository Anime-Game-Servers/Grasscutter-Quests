package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.item.upgrade.WeaponUpgradeReq;

public class HandlerWeaponUpgradeReq extends TypedPacketHandler<WeaponUpgradeReq> {
    @Override
    public void handle(GameSession session, byte[] header, WeaponUpgradeReq req) throws Exception {
        // Level up weapon
        session.getServer().getInventorySystem().upgradeWeapon(
            session.getPlayer(),
            req.getTargetWeaponGuid(),
            req.getFoodWeaponGuidList(),
            req.getItemParamList()
        );
    }
}
