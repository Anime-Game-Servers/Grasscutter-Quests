package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.EntityFightPropUpdateNotify;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PacketEntityFightPropUpdateNotify extends BaseTypedPacket<EntityFightPropUpdateNotify> {

	public PacketEntityFightPropUpdateNotify(GameEntity entity, FightProperty prop) {
        super(new EntityFightPropUpdateNotify());
        proto.setEntityId(entity.getId());
        proto.setFightPropMap(Map.of(prop.getId(), entity.getFightProperty(prop)));
	}

    public PacketEntityFightPropUpdateNotify(GameEntity entity, Collection<FightProperty> props) {
        super(new EntityFightPropUpdateNotify());
        proto.setEntityId(entity.getId());
        Map<Integer, Float> fightPropMap = new HashMap<>();
        props.forEach(p -> fightPropMap.put(p.getId(), entity.getFightProperty(p)));
        proto.setFightPropMap(fightPropMap);
	}
}
