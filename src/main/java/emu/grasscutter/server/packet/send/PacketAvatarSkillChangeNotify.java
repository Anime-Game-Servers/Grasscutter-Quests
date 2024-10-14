package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.skill.AvatarSkillChangeNotify;

public class PacketAvatarSkillChangeNotify extends BaseTypedPacket<AvatarSkillChangeNotify> {

    public PacketAvatarSkillChangeNotify(Avatar avatar, int skillId, int oldLevel, int curLevel) {
        super(new AvatarSkillChangeNotify());
        proto.setAvatarGuid(avatar.getGuid());
        proto.setEntityId(avatar.getEntityId());
        proto.setSkillDepotId(avatar.getSkillDepotId());
        proto.setAvatarSkillId(skillId);
        proto.setOldLevel(oldLevel);
        proto.setCurLevel(curLevel);
    }
}
