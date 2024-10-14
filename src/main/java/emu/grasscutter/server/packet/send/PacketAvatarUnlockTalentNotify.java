package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.skill.AvatarUnlockTalentNotify;

public class PacketAvatarUnlockTalentNotify extends BaseTypedPacket<AvatarUnlockTalentNotify> {
	public PacketAvatarUnlockTalentNotify(Avatar avatar, int talentId) {
        super(new AvatarUnlockTalentNotify());
        proto.setAvatarGuid(avatar.getGuid());
        proto.setEntityId(avatar.getEntityId());
        proto.setTalentId(talentId);
        proto.setSkillDepotId(avatar.getSkillDepotId());
	}
}
