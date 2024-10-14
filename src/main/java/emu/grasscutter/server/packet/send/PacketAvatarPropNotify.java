package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.properties.AvatarPropNotify;

import java.util.Map;

public class PacketAvatarPropNotify extends BaseTypedPacket<AvatarPropNotify> {
	public PacketAvatarPropNotify(Avatar avatar) {
        super(new AvatarPropNotify());
        proto.setAvatarGuid(avatar.getGuid());
        proto.setPropMap(Map.of(
            PlayerProperty.PROP_LEVEL.getId(), (long) avatar.getLevel(),
            PlayerProperty.PROP_EXP.getId(), (long) avatar.getExp(),
            PlayerProperty.PROP_BREAK_LEVEL.getId(), (long) avatar.getPromoteLevel(),
            PlayerProperty.PROP_SATIATION_VAL.getId(), 0L,
            PlayerProperty.PROP_SATIATION_PENALTY_TIME.getId(), 0L
        ));
	}

	public PacketAvatarPropNotify(Avatar avatar, PlayerProperty prop, int value) {
        super(new AvatarPropNotify());
        proto.setAvatarGuid(avatar.getGuid());
        proto.setPropMap(Map.of(prop.getId(), (long) value));
	}
}
