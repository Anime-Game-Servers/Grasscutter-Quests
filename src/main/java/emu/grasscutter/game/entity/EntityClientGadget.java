package emu.grasscutter.game.entity;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.config.ConfigEntityGadget;
import emu.grasscutter.data.binout.config.fields.ConfigAbilityData;
import emu.grasscutter.data.excels.GadgetData;
import emu.grasscutter.game.ability.AbilityManager;
import emu.grasscutter.game.entity.interfaces.ConfigAbilityDataAbilityEntity;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.utils.Position;
import emu.grasscutter.utils.ProtoHelper;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.battle.event.EvtCreateGadgetNotify;
import org.anime_game_servers.multi_proto.gi.messages.general.Vector;
import org.anime_game_servers.multi_proto.gi.messages.general.ability.AbilitySyncStateInfo;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.*;

import java.util.Collection;
import java.util.List;

public class EntityClientGadget extends EntityBaseGadget implements ConfigAbilityDataAbilityEntity {
    @Getter private final Player owner;

    @Getter(onMethod = @__(@Override))
    private final int gadgetId;

    @Getter private final int ownerEntityId;
    @Getter private final int targetEntityId;
    @Getter private final boolean asyncLoad;

    @Getter private final int originalOwnerEntityId;

    @Getter private final GadgetData gadgetData;
    private ConfigEntityGadget configGadget;

    public EntityClientGadget(Scene scene, Player player, EvtCreateGadgetNotify notify) {
        super(scene, new Position(notify.getInitPos()), new Position(notify.getInitEulerAngles()), notify.getCampId(), notify.getCampType());
        this.owner = player;
        this.id = notify.getEntityId();
        this.gadgetId = notify.getConfigId();
        this.ownerEntityId = notify.getPropOwnerEntityId();
        this.targetEntityId = notify.getTargetEntityId();
        this.asyncLoad = notify.isAsyncLoad();

        this.gadgetData = GameData.getGadgetDataMap().get(gadgetId);
        if (gadgetData!=null && gadgetData.getJsonName()!=null) {
            this.configGadget = GameData.getGadgetConfigData().get(gadgetData.getJsonName());
        }

        GameEntity owner = scene.getEntityById(this.ownerEntityId);
        if (owner instanceof EntityClientGadget ownerGadget) {
            this.originalOwnerEntityId = ownerGadget.getOriginalOwnerEntityId();
        } else {
            this.originalOwnerEntityId = this.ownerEntityId;
        }

        initAbilities();
    }

    @Override
    public Collection<ConfigAbilityData> getAbilityData() {
        return this.configGadget != null ? this.configGadget.getAbilities() : null;
    }

    @Override
    public AbilityManager getAbilityTargetManager() {
        return this.owner.getAbilityManager();
    }

    @Override
    public void onDeath(int killerId) {
        super.onDeath(killerId); // Invoke super class's onDeath() method.
    }

    @Override
    public Int2FloatMap getFightProperties() {
        return null;
    }

    @Override
    public SceneEntityInfo toProto() {
        val protoBornPos = new Vector();
        val aiInfo = new SceneEntityAiInfo(true, protoBornPos);
        val authority = new EntityAuthorityInfo(new AbilitySyncStateInfo(), new EntityRendererChangedInfo(), aiInfo, protoBornPos);

        val entityInfo = new SceneEntityInfo(ProtEntityType.PROT_ENTITY_GADGET, getId());
        entityInfo.setMotionInfo(new MotionInfo(getPosition().toProto(), getRotation().toProto(), new Vector()));
        entityInfo.setAnimatorParaList(List.of(new AnimatorParameterValueInfoPair()));
        entityInfo.setEntityClientData(new EntityClientData());
        entityInfo.setEntityAuthorityInfo(authority);
        entityInfo.setLifeState(1);

        val pair = new PropPair(PlayerProperty.PROP_LEVEL.getId(), ProtoHelper.newPropValue(PlayerProperty.PROP_LEVEL, 1));
        entityInfo.setPropList(List.of(pair));

        val clientGadget = new ClientGadgetInfo(this.getCampId(),this.getCampType());
        clientGadget.setOwnerEntityId(this.getOwnerEntityId());
        clientGadget.setTargetEntityId(this.getTargetEntityId());
        clientGadget.setAsyncLoad(this.isAsyncLoad());

        val gadgetInfo = new SceneGadgetInfo(this.getGadgetId());
        gadgetInfo.setOwnerEntityId(this.getOwnerEntityId());
        gadgetInfo.setEnableInteract(true);
        gadgetInfo.setContent(new SceneGadgetInfo.Content.ClientGadget(clientGadget));
        gadgetInfo.setPropOwnerEntityId(this.getOwnerEntityId());
        gadgetInfo.setAuthorityPeerId(this.getOwner().getPeerId());

        entityInfo.setEntity(new SceneEntityInfo.Entity.Gadget(gadgetInfo));

        return entityInfo;
    }
}
