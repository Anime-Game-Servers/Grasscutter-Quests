package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import org.anime_game_servers.multi_proto.gi.messages.general.avatar.AvatarSkillInfo;
import org.anime_game_servers.multi_proto.gi.messages.team.skill.AvatarSkillInfoNotify;

import java.util.HashMap;
import java.util.Map;

public class PacketAvatarSkillInfoNotify extends BaseTypedPacket<AvatarSkillInfoNotify> {
    public PacketAvatarSkillInfoNotify(long avatarGuid, Int2IntMap skillExtraChargeMap) {
        super(new AvatarSkillInfoNotify());
        proto.setGuid(avatarGuid);
        Map<Integer, AvatarSkillInfo> AvatarSkillInfoMap = new HashMap<>();
        skillExtraChargeMap.forEach((skillId, count) -> {
            var AvatarSkill = new AvatarSkillInfo();
            AvatarSkill.setMaxChargeCount(count);
            AvatarSkillInfoMap.put(skillId, AvatarSkill);
        });
        proto.setSkillMap(AvatarSkillInfoMap);
    }
}
