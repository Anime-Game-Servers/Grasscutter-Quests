package emu.grasscutter.game.entity;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.config.ConfigEntityGadget;
import emu.grasscutter.data.excels.GadgetData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.EntityIdType;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.utils.Position;
import emu.grasscutter.utils.ProtoHelper;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.Vector;
import org.anime_game_servers.multi_proto.gi.messages.general.ability.AbilitySyncStateInfo;
import org.anime_game_servers.multi_proto.gi.messages.general.vehicle.VehicleInfo;
import org.anime_game_servers.multi_proto.gi.messages.general.vehicle.VehicleMember;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EntityVehicle extends EntityBaseGadget {

    @Getter private final Player owner;
    @Getter(onMethod = @__(@Override))
    private final Int2FloatMap fightProperties;

    @Getter private final int pointId;
    @Getter private final int gadgetId;

    @Getter @Setter private float curStamina;
    @Getter private final List<VehicleMember> vehicleMembers;
    @Nullable @Getter private ConfigEntityGadget configGadget;

    public EntityVehicle(Scene scene, Player player, int gadgetId, int pointId, Position pos, Position rot) {
        super(scene, pos, rot);
        this.owner = player;
        this.id = getScene().getWorld().getNextEntityId(EntityIdType.GADGET);
        this.fightProperties = new Int2FloatOpenHashMap();
        this.gadgetId = gadgetId;
        this.pointId = pointId;
        this.curStamina = 240; // might be in configGadget.GCALKECLLLP.JBAKBEFIMBN.ANBMPHPOALP
        this.vehicleMembers = new ArrayList<>();
        GadgetData data = GameData.getGadgetDataMap().get(gadgetId);
        if (data != null && data.getJsonName() != null) {
            this.configGadget = GameData.getGadgetConfigData().get(data.getJsonName());
        }

        fillFightProps(configGadget);
    }

    @Override
    protected void fillFightProps(ConfigEntityGadget configGadget) {
        super.fillFightProps(configGadget);
        this.addFightProperty(FightProperty.FIGHT_PROP_CUR_SPEED, 0);
        this.addFightProperty(FightProperty.FIGHT_PROP_CHARGE_EFFICIENCY, 0);
    }

    @Override
    public SceneEntityInfo toProto() {

        val vehicle = new VehicleInfo();
        vehicle.setOwnerUid(this.owner.getUid());
        vehicle.setCurStamina(getCurStamina());

        val protoBornPos = getPosition().toProto();
        val protoPos = getPosition().toProto();
        val protoRot = getRotation().toProto();
        val aiInfo = new SceneEntityAiInfo(true, protoBornPos);
        val authority = new EntityAuthorityInfo(new AbilitySyncStateInfo(), new EntityRendererChangedInfo(), aiInfo, protoBornPos);

        val gadgetInfo = new SceneGadgetInfo(this.getGadgetId());
        gadgetInfo.setAuthorityPeerId(this.getOwner().getPeerId());
        gadgetInfo.setEnableInteract(true);
        gadgetInfo.setContent(new SceneGadgetInfo.Content.VehicleInfo(vehicle));

        val entityInfo = new SceneEntityInfo(ProtEntityType.PROT_ENTITY_GADGET, getId());
        entityInfo.setMotionInfo(new MotionInfo(protoPos, protoRot, new Vector()));
        entityInfo.setAnimatorParaList(List.of(new AnimatorParameterValueInfoPair()));
        entityInfo.setEntity(new SceneEntityInfo.Entity.Gadget(gadgetInfo));
        entityInfo.setEntityAuthorityInfo(authority);
        entityInfo.setLifeState(1);


        this.addAllFightPropsToEntityInfo(entityInfo);

        val pair = new PropPair(PlayerProperty.PROP_LEVEL.getId(), ProtoHelper.newPropValue(PlayerProperty.PROP_LEVEL, 47));
        entityInfo.setPropList(List.of(pair));

        return entityInfo;
    }
}
