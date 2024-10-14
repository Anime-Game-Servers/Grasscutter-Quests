package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.skill.AvatarSkillDepotChangeNotify;

import java.util.ArrayList;

public class PacketAvatarSkillDepotChangeNotify extends BaseTypedPacket<AvatarSkillDepotChangeNotify> {
    public PacketAvatarSkillDepotChangeNotify(Avatar avatar) {
        super(new AvatarSkillDepotChangeNotify());
        proto.setAvatarGuid(avatar.getGuid());
        proto.setEntityId(avatar.getEntityId());
        proto.setSkillDepotId(avatar.getSkillDepotId());
        proto.setTalentIdList(new ArrayList<>(avatar.getTalentIdList()));
        proto.setProudSkillList(new ArrayList<>(avatar.getProudSkillList()));
        proto.setCoreProudSkillLevel(avatar.getCoreProudSkillLevel());
        proto.setSkillLevelMap(avatar.getSkillLevelMap());
        proto.setProudSkillExtraLevelMap(avatar.getProudSkillBonusMap());
    }
}
