package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.skill.AvatarSkillMaxChargeCountNotify;

public class PacketAvatarSkillMaxChargeCountNotify extends BaseTypedPacket<AvatarSkillMaxChargeCountNotify> {

	public PacketAvatarSkillMaxChargeCountNotify(Avatar avatar, int skillId, int maxCharges) {
        super(new AvatarSkillMaxChargeCountNotify());
        proto.setAvatarGuid(avatar.getGuid());
        proto.setSkillId(skillId);
        proto.setMaxChargeCount(maxCharges);
	}
}
