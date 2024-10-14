package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.upgrade.AvatarUpgradeRsp;

import java.util.Map;

public class PacketAvatarUpgradeRsp extends BaseTypedPacket<AvatarUpgradeRsp> {

	public PacketAvatarUpgradeRsp(Avatar avatar, int oldLevel, Map<Integer, Float> oldFightPropMap) {
        super(new AvatarUpgradeRsp());
		this.buildHeader(0);
        proto.setAvatarGuid(avatar.getGuid());
        proto.setOldLevel(oldLevel);
        proto.setCurLevel(avatar.getLevel());
        proto.setOldFightPropMap(oldFightPropMap);
        proto.setCurFightPropMap(avatar.getFightProperties());
	}
}
