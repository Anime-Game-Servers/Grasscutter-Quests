package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.upgrade.AvatarPromoteReq;

public class HandlerAvatarPromoteReq extends TypedPacketHandler<AvatarPromoteReq> {
    @Override
    public void handle(GameSession session, byte[] header, AvatarPromoteReq req) throws Exception {
        // Ascend avatar
        session.getServer().getInventorySystem().promoteAvatar(session.getPlayer(), req.getGuid());
    }
}
