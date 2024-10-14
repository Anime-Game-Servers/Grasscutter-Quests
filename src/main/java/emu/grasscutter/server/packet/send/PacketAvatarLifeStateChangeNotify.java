package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.game.props.LifeState;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.PlayerDieType;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.properties.AvatarLifeStateChangeNotify;

public class PacketAvatarLifeStateChangeNotify extends BaseTypedPacket<AvatarLifeStateChangeNotify> {
	public PacketAvatarLifeStateChangeNotify(Avatar avatar) {
        super(new AvatarLifeStateChangeNotify());
        proto.setAvatarGuid(avatar.getGuid());
        proto.setLifeState(avatar.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP) > 0 ? LifeState.LIFE_ALIVE.getValue() : LifeState.LIFE_DEAD.getValue());
    }

	public PacketAvatarLifeStateChangeNotify(Avatar avatar, int attackerId, LifeState lifeState) {
        super(new AvatarLifeStateChangeNotify());
        proto.setAvatarGuid(avatar.getGuid());
        proto.setLifeState(lifeState.getValue());
        proto.setMoveReliableSeq(attackerId);
	}

	public PacketAvatarLifeStateChangeNotify(Avatar avatar, LifeState lifeState, PlayerDieType dieType) {
		this(avatar, lifeState, null, "", dieType);
	}

	public PacketAvatarLifeStateChangeNotify(Avatar avatar, LifeState lifeState, GameEntity sourceEntity,
											 String attackTag, PlayerDieType dieType) {
        super(new AvatarLifeStateChangeNotify());
		proto.setAvatarGuid(avatar.getGuid());
		proto.setLifeState(lifeState.getValue());
		if (sourceEntity != null) {
			proto.setSourceEntityId(sourceEntity.getId());
		}
		proto.setDieType(dieType);
		proto.setAttackTag((attackTag));
	}

}
