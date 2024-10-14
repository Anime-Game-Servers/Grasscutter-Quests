package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.skill.AvatarSkillUpgradeRsp;

public class PacketAvatarSkillUpgradeRsp extends BaseTypedPacket<AvatarSkillUpgradeRsp> {

	public PacketAvatarSkillUpgradeRsp(Avatar avatar, int skillId, int oldLevel, int newLevel) {
        super(new AvatarSkillUpgradeRsp());
        proto.setAvatarGuid(avatar.getGuid());
        proto.setAvatarSkillId(skillId);
        proto.setOldLevel(oldLevel);
        proto.setCurLevel(newLevel);
	}
}
