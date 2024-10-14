package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.properties.AvatarFightPropUpdateNotify;

import java.util.Map;

public class PacketAvatarFightPropUpdateNotify extends BaseTypedPacket<AvatarFightPropUpdateNotify> {

	public PacketAvatarFightPropUpdateNotify(Avatar avatar, FightProperty prop) {
        this(avatar, Map.of(prop.getId(), avatar.getFightProperty(prop)));
	}

	public PacketAvatarFightPropUpdateNotify(Avatar avatar, Map<Integer, Float> propUpdateList) {
        super(new AvatarFightPropUpdateNotify());
        proto.setAvatarGuid(avatar.getGuid());
        proto.setFightPropMap(propUpdateList);

	}
}
