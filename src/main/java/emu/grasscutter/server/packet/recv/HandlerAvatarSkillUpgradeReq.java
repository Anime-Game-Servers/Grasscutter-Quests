package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.team.skill.AvatarSkillUpgradeReq;

public class HandlerAvatarSkillUpgradeReq extends TypedPacketHandler<AvatarSkillUpgradeReq> {

    @Override
    public void handle(GameSession session, byte[] header, AvatarSkillUpgradeReq req) throws Exception {
        // Sanity checks
        var avatar = session.getPlayer().getAvatars().getAvatarByGuid(req.getAvatarGuid());
        if (avatar == null) return;
        // Level up avatar talent
        avatar.upgradeSkill(req.getAvatarSkillId());
    }
}
