package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.skill.ProudSkillExtraLevelNotify;

public class PacketProudSkillExtraLevelNotify extends BaseTypedPacket<ProudSkillExtraLevelNotify> {
    public PacketProudSkillExtraLevelNotify(Avatar avatar, int talentIndex) {
        super(new ProudSkillExtraLevelNotify());
        proto.setAvatarGuid(avatar.getGuid());
        proto.setTalentType(3); // Talent type = 3
        proto.setTalentIndex(talentIndex);
        proto.setExtraLevel(3);
	}
}
