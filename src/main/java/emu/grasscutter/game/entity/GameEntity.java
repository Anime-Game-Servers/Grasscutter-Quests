package emu.grasscutter.game.entity;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.ability.AbilityModifierController;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.*;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.game.world.SpawnDataEntry;
import emu.grasscutter.game.world.World;
import emu.grasscutter.scripts.data.controller.EntityController;
import emu.grasscutter.server.event.entity.EntityDamageEvent;
import emu.grasscutter.server.event.entity.EntityDeathEvent;
import emu.grasscutter.server.packet.send.PacketEntityFightPropUpdateNotify;
import emu.grasscutter.utils.Position;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.gadget.GadgetInteractReq;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.FightPropPair;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.MotionInfo;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.MotionState;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.SceneEntityInfo;

import javax.annotation.Nullable;
import java.util.*;

public abstract class GameEntity {
    @Getter private final Scene scene;
    @Getter protected int id;
    @Getter @Setter private SpawnDataEntry spawnEntry;

    @Getter @Setter private int blockId;
    @Getter @Setter private int configId;
    @Getter @Setter private int groupId;

    @Getter @Setter private MotionState motionState;
    @Getter @Setter private int lastMoveSceneTimeMs;
    @Getter @Setter private int lastMoveReliableSeq;

    @Getter @Setter private boolean lockHP;

    // Lua controller for specific actions
    @Getter @Setter private EntityController entityController;
    @Getter private ElementType lastAttackType = ElementType.None;

    @Getter private List<Ability> instancedAbilities = new ArrayList<>();
    @Getter private Int2ObjectMap<AbilityModifierController> instancedModifiers = new Int2ObjectOpenHashMap<>();
    @Getter private Map<String, Float> globalAbilityValues = new HashMap<>();

    public GameEntity(Scene scene) {
        this.scene = scene;
        this.motionState = MotionState.MOTION_NONE;
    }

    public EntityType getEntityType() {
        return EntityIdType.fromEntityId(this.getId()).getType();
    }

    public abstract int getEntityTypeId();

    public World getWorld() {
        return this.getScene().getWorld();
    }

    public boolean isAlive() {
        return true;
    }

    public LifeState getLifeState() {
        return this.isAlive() ? LifeState.LIFE_ALIVE : LifeState.LIFE_DEAD;
    }

    @Nullable
    public abstract Int2FloatMap getFightProperties();
    public Optional<Int2FloatMap> getFightPropertiesOpt() {
        return Optional.ofNullable(this.getFightProperties());
    }

    public abstract Position getPosition();

    public abstract Position getRotation();

    public void setFightProperty(FightProperty prop, float value) {
        this.setFightProperty(prop.getId(), value);
    }

    public void setFightProperty(int id, float value) {
        this.getFightPropertiesOpt().ifPresent(
            props -> props.put(id, value)
        );
    }

    public void addFightProperty(FightProperty prop, float value) {
        this.getFightPropertiesOpt().ifPresent(
            props -> props.put(prop.getId(), props.getOrDefault(prop.getId(), 0f) + value)
        );
    }

    public float getFightProperty(FightProperty prop) {
        val props = this.getFightProperties();
        if(props == null) return 0f;
        return props.get(prop.getId());
    }

    public boolean hasFightProperty(FightProperty prop) {
        val props = this.getFightProperties();
        if(props == null) return false;
        return props.containsKey(prop.getId());
    }

    public void addAllFightPropsToEntityInfo(SceneEntityInfo entityInfo) {
        this.getFightPropertiesOpt().ifPresent(map -> entityInfo.setFightPropList(map.int2FloatEntrySet().stream()
            .filter(entry -> entry.getIntKey() != 0)
            .map(entry -> new FightPropPair(entry.getIntKey(), entry.getFloatValue())).toList()));
    }

    protected MotionInfo getMotionInfo() {
        val speed = new org.anime_game_servers.multi_proto.gi.messages.general.Vector();
        return new MotionInfo(this.getPosition().toProto(), this.getRotation().toProto(), speed, this.getMotionState());
    }

    public float heal(float amount) {
        return heal(amount, false);
    }

    public float heal(float amount, boolean mute) {
        if (this.getFightProperties() == null) {
            return 0f;
        }

        float curHp = this.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP);
        float maxHp = this.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP);

        if (curHp >= maxHp) {
            return 0f;
        }

        float healed = Math.min(maxHp - curHp, amount);
        this.addFightProperty(FightProperty.FIGHT_PROP_CUR_HP, healed);

        this.getScene().broadcastPacket(new PacketEntityFightPropUpdateNotify(this, FightProperty.FIGHT_PROP_CUR_HP));

        return healed;
    }

    public void damage(float amount) {
        this.damage(amount, 0, ElementType.None);
    }

    public void damage(float amount, ElementType attackType) {
        this.damage(amount, 0, attackType);
    }

    public void damage(float amount, int killerId, ElementType attackType) {
        // Check if the entity has properties.
        if (!hasFightProperty(FightProperty.FIGHT_PROP_CUR_HP)) {
            return;
        }

        // Invoke entity damage event.
        EntityDamageEvent event = new EntityDamageEvent(this, amount, attackType, this.getScene().getEntityById(killerId));
        event.call();
        if (event.isCanceled()) {
            return; // If the event is canceled, do not damage the entity.
        }

        float curHp = getFightProperty(FightProperty.FIGHT_PROP_CUR_HP);
        if (curHp != Float.POSITIVE_INFINITY && !lockHP || lockHP && curHp <= event.getDamage()) {
            // Add negative HP to the current HP property.
            this.addFightProperty(FightProperty.FIGHT_PROP_CUR_HP, -(event.getDamage()));
        }

        this.lastAttackType = attackType;

        // Check if dead
        boolean isDead = false;
        if (this.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP) <= 0f) {
            this.setFightProperty(FightProperty.FIGHT_PROP_CUR_HP, 0f);
            isDead = true;
        }
        callLuaHPEvent(event);
        callAbilityBeHurt(event);

        // Packets
        this.getScene().broadcastPacket(new PacketEntityFightPropUpdateNotify(this, FightProperty.FIGHT_PROP_CUR_HP));

        // Check if dead.
        if (isDead) {
            this.getScene().killEntity(this, killerId);
        }
    }

    public void callLuaHPEvent(EntityDamageEvent event) {
        if (entityController != null) {
            entityController.onBeHurt(this, event.getAttackElementType(), true);//todo is host handling
        }
    }

    public void callAbilityBeHurt(EntityDamageEvent event) {
        //instancedAbilities.forEach(ability -> ability.onBeingHit(event));
    }

    /**
     * Move this entity to a new position.
     *
     * @param position The new position.
     * @param rotation The new rotation.
     */
    public void move(Position position, Position rotation) {
        // Set the position and rotation.
        this.getPosition().set(position);
        this.getRotation().set(rotation);
    }

    /**
     * Called when a player interacts with this entity
     *
     * @param player      Player that is interacting with this entity
     * @param interactReq Interact request protobuf data
     */
    public void onInteract(Player player, GadgetInteractReq interactReq) {

    }

    /**
     * Called when this entity is added to the world
     */
    public void onCreate() {

    }

    /**
     * Called right after this entity is added to the world
     */
    public void afterCreate(List<Player> players) {

    }

    public void onRemoved() {

    }

    private int[] parseCountRange(String range) {
        var split = range.split(";");
        if(split.length == 1) return new int[] {Integer.parseInt(split[0]), Integer.parseInt(split[0])};
        return new int[] {Integer.parseInt(split[0]), Integer.parseInt(split[1])};
    }

    public boolean dropSubfieldItem(int dropId) {
        var drop = GameData.getDropSubfieldMappingMap().get(dropId);
        if(drop == null) return false;
        var dropTableEntry = GameData.getDropTableExcelConfigDataMap().get(drop.getItemId());
        if(dropTableEntry == null) return false;

        var dropRandom = getScene().getWorld().getWorldRandomGenerator();

        Int2ObjectMap<Integer> itemsToDrop = new Int2ObjectOpenHashMap<>();
        switch(dropTableEntry.getRandomType()) {
            case 0: //select one
                {
                    int weightCount = 0;
                    for(var entry : dropTableEntry.getDropVec()) weightCount += entry.getWeight();

                    int randomValue = dropRandom.nextInt(weightCount);

                    weightCount = 0;
                    for(var entry : dropTableEntry.getDropVec()) {
                        if(randomValue >= weightCount && randomValue < (weightCount + entry.getWeight())) {
                            var countRange = parseCountRange(entry.getCountRange());
                            itemsToDrop.put(entry.getItemId(), Integer.valueOf((dropRandom.nextBoolean() ? countRange[0] : countRange[1])));
                        }
                    }
                }
                break;
            case 1: //Select various
                {
                    for(var entry : dropTableEntry.getDropVec()) {
                        if(entry.getWeight() < dropRandom.nextInt(10000)) {
                            var countRange = parseCountRange(entry.getCountRange());
                            itemsToDrop.put(entry.getItemId(), Integer.valueOf((dropRandom.nextBoolean() ? countRange[0] : countRange[1])));
                        }
                    }
                }
                break;
        }

        for (var entry : itemsToDrop.int2ObjectEntrySet()) {
            EntityItem item = new EntityItem(
                scene,
                null,
                GameData.getItemDataMap().get(entry.getIntKey()),
                getPosition().nearby2d(1f).addY(0.5f),
                entry.getValue(),
                true);

            scene.addEntity(item);
        }

        return true;
    }

    public boolean dropSubfield(String subfieldName) {
        var subfieldMapping = GameData.getSubfieldMappingMap().get(getEntityTypeId());
        if(subfieldMapping == null || subfieldMapping.getSubfields() == null) return false;

        for(var entry : subfieldMapping.getSubfields()) {
            if(entry.getSubfieldName().compareTo(subfieldName) == 0) {
                return dropSubfieldItem(entry.getDrop_id());
            }
        }

        return false;
    }

    public void onTick(int sceneTime) {
        if (entityController != null) {
            entityController.onTimer(this, sceneTime);
        }
    }

    public int onClientExecuteRequest(int param1, int param2, int param3) {
        if (entityController != null) {
            return entityController.onClientExecuteRequest(this, param1, param2, param3);
        }
        return 0;
    }

    /**
     * Called when this entity dies
     *
     * @param killerId Entity id of the entity that killed this entity
     */
    public void onDeath(int killerId) {
        // Invoke entity death event.
        EntityDeathEvent event = new EntityDeathEvent(this, killerId);
        event.call();

        if (entityController != null) {
            entityController.onDie(this, getLastAttackType());
        }
    }

    public abstract SceneEntityInfo toProto();
}
