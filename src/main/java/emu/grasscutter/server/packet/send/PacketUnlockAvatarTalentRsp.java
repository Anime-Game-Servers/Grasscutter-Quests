package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.skill.UnlockAvatarTalentRsp;

public class PacketUnlockAvatarTalentRsp extends BaseTypedPacket<UnlockAvatarTalentRsp> {
	public PacketUnlockAvatarTalentRsp(Avatar avatar, int talentId) {
        super(new UnlockAvatarTalentRsp());
        proto.setAvatarGuid(avatar.getGuid());
        proto.setTalentId(talentId);
	}
}
