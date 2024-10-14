package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.PropChangeReason;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.ChangeEnergyReason;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.ChangeHpReason;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.EntityFightPropChangeReasonNotify;

import java.util.List;

public class PacketEntityFightPropChangeReasonNotify extends BaseTypedPacket<EntityFightPropChangeReasonNotify> {

    public PacketEntityFightPropChangeReasonNotify(GameEntity entity, FightProperty prop, Float value, List<Integer> param, PropChangeReason reason, ChangeHpReason changeHpReason) {
        super(new EntityFightPropChangeReasonNotify());
        proto.setEntityId(entity.getId());
        proto.setPropType(prop.getId());
        proto.setPropDelta(value);
        proto.setReason(reason);
        proto.setChangeHpReason(changeHpReason);
        proto.setParamList(param);
    }

    public PacketEntityFightPropChangeReasonNotify(GameEntity entity, FightProperty prop, Float value, PropChangeReason reason, ChangeHpReason changeHpReason) {
        super(new EntityFightPropChangeReasonNotify());
        proto.setEntityId(entity.getId());
        proto.setPropType(prop.getId());
        proto.setPropDelta(value);
        proto.setReason(reason);
        proto.setChangeHpReason(changeHpReason);
    }

    public PacketEntityFightPropChangeReasonNotify(GameEntity entity, FightProperty prop, Float value, PropChangeReason reason) {
        super(new EntityFightPropChangeReasonNotify());
        proto.setEntityId(entity.getId());
        proto.setPropType(prop.getId());
        proto.setPropDelta(value);
        proto.setReason(reason);
    }

    public PacketEntityFightPropChangeReasonNotify(GameEntity entity, FightProperty prop, Float value, ChangeEnergyReason reason) {
        super(new EntityFightPropChangeReasonNotify());
        proto.setEntityId(entity.getId());
        proto.setPropType(prop.getId());
        proto.setPropDelta(value);
        proto.setChangeEnergyReason(reason);

    }
}
