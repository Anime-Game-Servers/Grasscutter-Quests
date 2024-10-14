package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.AvatarDieAnimationEndRsp;

public class PacketAvatarDieAnimationEndRsp extends BaseTypedPacket<AvatarDieAnimationEndRsp> {

	public PacketAvatarDieAnimationEndRsp(long dieGuid, int skillId) {
        super(new AvatarDieAnimationEndRsp());
        proto.setDieGuid(dieGuid);
        proto.setSkillId(skillId);
	}
}
