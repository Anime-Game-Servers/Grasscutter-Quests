package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.skill.ProudSkillChangeNotify;

public class PacketProudSkillChangeNotify extends BaseTypedPacket<ProudSkillChangeNotify> {
	public PacketProudSkillChangeNotify(Avatar avatar) {
        super(new ProudSkillChangeNotify());
        proto.setAvatarGuid(avatar.getGuid());
        proto.setEntityId(avatar.getEntityId());
        proto.setSkillDepotId(avatar.getSkillDepotId());
        proto.setProudSkillList(avatar.getProudSkillList().stream().toList());
	}
}
