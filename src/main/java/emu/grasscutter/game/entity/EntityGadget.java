package emu.grasscutter.game.entity;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.config.ConfigEntityGadget;
import emu.grasscutter.data.binout.config.fields.ConfigAbilityData;
import emu.grasscutter.data.excels.GadgetData;
import emu.grasscutter.game.ability.AbilityManager;
import emu.grasscutter.game.entity.gadget.*;
import emu.grasscutter.game.entity.gadget.platform.BaseRoute;
import emu.grasscutter.game.entity.gadget.platform.ConfigRoute;
import emu.grasscutter.game.entity.interfaces.ConfigAbilityDataAbilityEntity;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.EntityIdType;
import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.game.world.SceneGroupInstance;
import emu.grasscutter.scripts.EntityControllerScriptManager;
import emu.grasscutter.server.packet.send.PacketGadgetStateNotify;
import emu.grasscutter.server.packet.send.PacketPlatformStartRouteNotify;
import emu.grasscutter.server.packet.send.PacketPlatformStopRouteNotify;
import emu.grasscutter.server.packet.send.PacketSceneTimeNotify;
import emu.grasscutter.utils.Position;
import emu.grasscutter.utils.ProtoHelper;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.anime_game_servers.gi_lua.models.ScriptArgs;
import org.anime_game_servers.gi_lua.models.constants.EventType;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGadget;
import org.anime_game_servers.multi_proto.gi.messages.gadget.GadgetInteractReq;
import org.anime_game_servers.multi_proto.gi.messages.general.ability.AbilitySyncStateInfo;
import org.anime_game_servers.multi_proto.gi.messages.scene.VisionType;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ToString(callSuper = true)
public class EntityGadget extends EntityBaseGadget implements ConfigAbilityDataAbilityEntity {
    @Getter private final GadgetData gadgetData;
    @Getter(onMethod = @__(@Override)) @Setter
    private int gadgetId;
    @Getter private final Position bornPos;
    @Getter private final Position bornRot;
    @Getter @Setter private GameEntity owner = null;
    @Getter @Setter private List<GameEntity> children = new ArrayList<>();

    @Getter private int state;
    @Getter @Setter private int pointType;
    @Getter private GadgetContent content;
    @Getter(onMethod = @__(@Override), lazy = true)
    private final Int2FloatMap fightProperties = new Int2FloatOpenHashMap();
    @Getter @Setter private SceneGadget metaGadget;
    @Nullable @Getter
    private ConfigEntityGadget configGadget;
    @Getter @Setter private BaseRoute routeConfig;

    @Getter @Setter private int stopValue = 0; //Controller related, inited to zero
    @Getter @Setter private int startValue = 0; //Controller related, inited to zero
    @Getter @Setter private int ticksSinceChange;

    @Getter private boolean interactEnabled = true;


    public EntityGadget(Scene scene, int gadgetId, Position pos) {
        this(scene, gadgetId, pos, null, null);
    }

    public EntityGadget(Scene scene, int gadgetId, Position pos, Position rot) {
        this(scene, gadgetId, pos, rot, null);
    }

    public EntityGadget(Scene scene, int gadgetId, Position pos, Position rot, int campId, int campType) {
        this(scene, gadgetId, pos, rot, null, campId, campType);
    }

    public EntityGadget(Scene scene, int gadgetId, Position pos, Position rot, GadgetContent content) {
        this(scene, gadgetId, pos, rot, content, 0, 0);
    }

    public EntityGadget(Scene scene, int gadgetId, Position pos, Position rot, GadgetContent content, int campId, int campType) {
        super(scene, pos, rot, campId, campType);
        this.gadgetData = GameData.getGadgetDataMap().get(gadgetId);
        if (gadgetData!=null && gadgetData.getJsonName()!=null) {
            this.configGadget = GameData.getGadgetConfigData().get(gadgetData.getJsonName());
        }
        this.id = this.getScene().getWorld().getNextEntityId(EntityIdType.GADGET);
        this.gadgetId = gadgetId;
        this.content = content;
        this.bornPos = this.getPosition().clone();
        this.bornRot = this.getRotation().clone();
        fillFightProps(configGadget);
        if(GameData.getGadgetMappingMap().containsKey(gadgetId)) {
            String controllerName = GameData.getGadgetMappingMap().get(gadgetId).getServerController();
            setEntityController(EntityControllerScriptManager.getGadgetController(controllerName));
            if(getEntityController() == null) {
                Grasscutter.getLogger().warn("Gadget controller {} not found", controllerName);
            }
        }

        initAbilities(); //TODO: move this
    }


    //TODO: handle predynamic, static and dynamic here
    @Override
    public Collection<ConfigAbilityData> getAbilityData() {
        return this.configGadget != null ? this.configGadget.getAbilities() : null;
    }

    @Override
    public AbilityManager getAbilityTargetManager() {
        return getWorld().getHost().getAbilityManager();
    }

    public void setInteractEnabled(boolean enable) {
        this.interactEnabled = enable;
        this.getScene().broadcastPacket(new PacketGadgetStateNotify(this, this.getState())); //Update the interact
    }

    public void setState(int state) {
        this.state = state;
        //Cache the gadget state
        if(metaGadget != null && metaGadget.getGroupId() > 0) {
            var instance = getScene().getScriptManager().getCachedGroupInstanceById(metaGadget.getGroupId());
            if(instance != null) instance.cacheGadgetState(metaGadget, state);
        }
    }

    public void updateState(int state) {
        if(state == this.getState()) return; //Don't triggers events
        val oldState = this.getState();
        this.setState(state);
        ticksSinceChange = getScene().getSceneTimeSeconds();
        this.getScene().broadcastPacket(new PacketGadgetStateNotify(this, state));
        getScene().getScriptManager().callEvent(new ScriptArgs(this.getGroupId(), EventType.EVENT_GADGET_STATE_CHANGE)
            .setParam1(state).setParam2(this.getConfigId()).setParam3(oldState).setSourceEntityId(getId()));
    }

    @Deprecated(forRemoval = true) // Dont use!
    public void setContent(GadgetContent content) {
        this.content = this.content == null ? content : this.content;
    }

    // TODO refactor
    public void buildContent() {
        if (this.getContent() != null || this.getGadgetData() == null || this.getGadgetData().getType() == null) {
            return;
        }

        this.content = switch (this.getGadgetData().getType()) {
            case GatherPoint -> new GadgetGatherPoint(this);
            case GatherObject -> new GadgetGatherObject(this);
            case Worktop, SealGadget -> new GadgetWorktop(this);
            case RewardStatue -> new GadgetRewardStatue(this);
            case Chest -> new GadgetChest(this);
            case Gadget -> new GadgetObject(this);
            case Screen -> new GadgetScreen(this);
            case ViewPoint -> new GadgetViewPoint(this);
            default -> null;
        };
    }

    @Override
    public void onInteract(Player player, GadgetInteractReq interactReq) {
        if(!interactEnabled) return;

        if (this.getContent() == null) {
            return;
        }

        boolean shouldDelete = this.getContent().onInteract(player, interactReq);

        if (shouldDelete) {
            this.getScene().killEntity(this);
        }
    }

    @Override
    public void onCreate() {
        // Lua event
        getScene().getScriptManager().callEvent(new ScriptArgs(this.getGroupId(), EventType.EVENT_GADGET_CREATE, this.getConfigId())
            .setSourceEntityId(this.getId()));
    }

    @Override
    public void afterCreate(List<Player> players) {
        if (this.getMetaGadget() != null && !this.getMetaGadget().isStartRoute()) return;
        if (this.routeConfig == null) return;
        this.routeConfig.startRoute(this.getScene());
        players.forEach(p -> p.sendPacket(new PacketPlatformStartRouteNotify(this)));
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        if(!children.isEmpty()) {
            getScene().removeEntities(children, VisionType.VISION_REMOVE);
            children.clear();
        }
    }

    @Override
    public void onDeath(int killerId) {
        super.onDeath(killerId); // Invoke super class's onDeath() method.

        if (this.getSpawnEntry() != null) {
            this.getScene().getDeadSpawnedEntities().add(getSpawnEntry());
        }
        getScene().getScriptManager().callEvent(new ScriptArgs(this.getGroupId(), EventType.EVENT_ANY_GADGET_DIE, this.getConfigId()));

        SceneGroupInstance groupInstance = getScene().getScriptManager().getCachedGroupInstanceById(this.getGroupId());
        if(groupInstance != null && metaGadget != null)
            groupInstance.getDeadEntities().add(metaGadget.getConfigId());

        val hostBlossom = getScene().getWorld().getHost().getBlossomManager();
        val removedChest = hostBlossom.getSpawnedChest().remove(getConfigId());
        if (removedChest != null) {
            Grasscutter.getLogger().info("[EntityGadget] Removing Blossom Chest: {}, {}", getGroupId(), getConfigId());
            getScene().unregisterDynamicGroup(getGroupId());
            getScene().getScriptManager().callEvent(
                new ScriptArgs(getGroupId(), EventType.EVENT_BLOSSOM_CHEST_DIE, getConfigId()));
            hostBlossom.buildNextCamp(getGroupId());
        }
    }

    public boolean startPlatform(){
        if(routeConfig == null){
            return false;
        }

        if (routeConfig.isStarted()) {
            return true;
        }

        schedulePlatform();

        getScene().broadcastPacket(new PacketSceneTimeNotify(getScene()));
        routeConfig.startRoute(getScene());
        getScene().broadcastPacket(new PacketPlatformStartRouteNotify(this));

        return true;
    }

    public boolean schedulePlatform() {
        if (!(routeConfig instanceof ConfigRoute configRoute)) {
            return false;
        }

        var route = this.getScene().getSceneRouteById(configRoute.getRouteId());

        if (route == null) {
            return false;
        }

        var points = route.getPoints();

        //restart platform if at end and told to start
        if (configRoute.getStartIndex() == points.length - 1) {
            configRoute.setStartIndex(0);
        }

        val currIndex = configRoute.getStartIndex();
        if (currIndex == 0) {
            this.getScene().callPlatformEvent(this.getId());
        }

        double distance = points[currIndex].getPos().computeDistance(points[currIndex + 1].getPos());
        double time = 1000 * distance / points[currIndex].getTargetVelocity();
        time += this.getScene().getSceneTime();
        this.getScene().getScheduledPlatforms().put(this.getId(), time);

        return true;
    }

    public boolean stopPlatform(){
        if(routeConfig == null){
            return false;
        }
        this.getScene().getScheduledPlatforms().remove(this.getId());
        routeConfig.stopRoute(getScene());
        getScene().broadcastPacket(new PacketPlatformStopRouteNotify(this));

        return true;
    }

    @Override
    public SceneEntityInfo toProto() {
        val protoBornPos = this.bornPos.toProto();
        val protoCurPos = getPosition().toProto();
        val protoCurRot = getRotation().toProto();
        val authority = new EntityAuthorityInfo(new AbilitySyncStateInfo(), new EntityRendererChangedInfo(),
            new SceneEntityAiInfo(true, protoBornPos), protoBornPos);

        val entityInfo = new SceneEntityInfo(ProtEntityType.PROT_ENTITY_GADGET, getId());
        entityInfo.setMotionInfo(new MotionInfo(protoCurPos, protoCurRot, new org.anime_game_servers.multi_proto.gi.messages.general.Vector()));
        entityInfo.setAnimatorParaList(List.of(new AnimatorParameterValueInfoPair()));
        entityInfo.setEntityClientData(new EntityClientData());
        entityInfo.setEntityAuthorityInfo(authority);
        entityInfo.setLifeState(1);

        val pair = new PropPair(PlayerProperty.PROP_LEVEL.getId(), ProtoHelper.newPropValue(PlayerProperty.PROP_LEVEL, 1));
        entityInfo.setPropList(List.of(pair));

        // We do not use the getter to null check because the getter will create a fight prop map if it is null
        if (this.fightProperties != null) {
            addAllFightPropsToEntityInfo(entityInfo);
        }

        val gadgetInfo = new SceneGadgetInfo(this.getGadgetId(), this.getGroupId(), this.getConfigId());
        gadgetInfo.setGadgetState(this.getState());
        gadgetInfo.setEnableInteract(this.interactEnabled);
        gadgetInfo.setAuthorityPeerId(this.getScene().getWorld().getHostPeerId());

        if (this.metaGadget != null) {
            gadgetInfo.setDraftId(this.metaGadget.getDraftId());
            gadgetInfo.setInteractId(this.metaGadget.getInteractId());
        }

        if(owner != null){
            gadgetInfo.setOwnerEntityId(owner.getId());
        }

        if (this.getContent() != null) {
            this.getContent().onBuildProto(gadgetInfo);
        }

        if(routeConfig!=null){
            gadgetInfo.setPlatform(getPlatformInfo());
        }

        entityInfo.setEntity(new SceneEntityInfo.Entity.Gadget(gadgetInfo));

        return entityInfo;
    }

    public PlatformInfo getPlatformInfo(){
        if(routeConfig != null){
            return routeConfig.toProto();
        }

        return new PlatformInfo();
    }
}
