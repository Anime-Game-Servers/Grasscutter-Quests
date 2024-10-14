package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.item.upgrade.WeaponAwakenReq;

public class HandlerWeaponAwakenReq extends TypedPacketHandler<WeaponAwakenReq> {
    @Override
    public void handle(GameSession session, byte[] header, WeaponAwakenReq req) throws Exception {
        session.getServer().getInventorySystem().refineWeapon(session.getPlayer(), req.getTargetWeaponGuid(), req.getItemGuid());
    }

}
