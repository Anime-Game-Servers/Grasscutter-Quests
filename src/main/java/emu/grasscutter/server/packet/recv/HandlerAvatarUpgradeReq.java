package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.upgrade.AvatarUpgradeReq;

public class HandlerAvatarUpgradeReq extends TypedPacketHandler<AvatarUpgradeReq> {

    @Override
    public void handle(GameSession session, byte[] header, AvatarUpgradeReq req) throws Exception {
        // Level up avatar
        session.getServer().getInventorySystem().upgradeAvatar(
                session.getPlayer(),
                req.getAvatarGuid(),
                req.getItemId(),
                req.getCount()
        );
    }

}
