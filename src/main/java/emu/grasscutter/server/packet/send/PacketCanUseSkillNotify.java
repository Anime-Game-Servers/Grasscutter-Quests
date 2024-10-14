package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.skill.CanUseSkillNotify;

public class PacketCanUseSkillNotify extends BaseTypedPacket<CanUseSkillNotify> {
    public PacketCanUseSkillNotify(boolean canUseSkill) {
        super(new CanUseSkillNotify());
        proto.setCanUseSkill(canUseSkill);
    }
}
