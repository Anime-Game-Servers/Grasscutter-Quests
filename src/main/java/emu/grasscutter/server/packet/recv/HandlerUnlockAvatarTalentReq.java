package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.team.skill.UnlockAvatarTalentReq;

public class HandlerUnlockAvatarTalentReq extends TypedPacketHandler<UnlockAvatarTalentReq> {
    @Override
    public void handle(GameSession session, byte[] header, UnlockAvatarTalentReq req) throws Exception {
        var avatar = session.getPlayer().getAvatars().getAvatarByGuid(req.getAvatarGuid());
        if (avatar != null) avatar.unlockConstellation(req.getTalentId());
    }
}
