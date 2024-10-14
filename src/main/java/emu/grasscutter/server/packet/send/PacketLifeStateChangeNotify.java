package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.props.LifeState;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.PlayerDieType;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.LifeStateChangeNotify;

public class PacketLifeStateChangeNotify extends BaseTypedPacket<LifeStateChangeNotify> {
	public PacketLifeStateChangeNotify(GameEntity target, LifeState lifeState) {
        super(new LifeStateChangeNotify());
        proto.setEntityId(target.getId());
        proto.setLifeState(lifeState.getValue());
	}

	public PacketLifeStateChangeNotify(GameEntity attacker, GameEntity target, LifeState lifeState) {
        super(new LifeStateChangeNotify());
        proto.setEntityId(target.getId());
        proto.setLifeState(lifeState.getValue());
        proto.setSourceEntityId(attacker.getId());
	}

	public PacketLifeStateChangeNotify(int attackerId, GameEntity target, LifeState lifeState) {
        super(new LifeStateChangeNotify());
        proto.setEntityId(target.getId());
        proto.setLifeState(lifeState.getValue());
        proto.setSourceEntityId(attackerId);
	}

	public PacketLifeStateChangeNotify(GameEntity entity, LifeState lifeState, PlayerDieType dieType) {
		this(entity, lifeState, null, "", dieType);
	}

	public PacketLifeStateChangeNotify(GameEntity entity, LifeState lifeState, GameEntity sourceEntity,
									   String attackTag, PlayerDieType dieType) {
        super(new LifeStateChangeNotify());
		proto.setEntityId(entity.getId());
		proto.setLifeState(lifeState.getValue());
		if (sourceEntity != null) {
			proto.setSourceEntityId(sourceEntity.getId());
		}
		proto.setAttackTag(attackTag);
		proto.setDieType(dieType);
	}
}
