package emu.grasscutter.game.entity;

import emu.grasscutter.GameConstants;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.AvatarData;
import emu.grasscutter.data.excels.AvatarSkillDepotData;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.inventory.EquipType;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.EntityIdType;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.game.world.Scene;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.ChangeEnergyReason;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.ChangeHpReason;
import org.anime_game_servers.multi_proto.gi.messages.general.PlayerDieType;
import org.anime_game_servers.multi_proto.gi.messages.general.PropChangeReason;
import emu.grasscutter.server.event.player.PlayerMoveEvent;
import emu.grasscutter.server.packet.send.PacketEntityFightPropChangeReasonNotify;
import emu.grasscutter.server.packet.send.PacketEntityFightPropUpdateNotify;
import emu.grasscutter.utils.Position;
import emu.grasscutter.utils.ProtoHelper;
import emu.grasscutter.utils.Utils;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.ability.AbilityControlBlock;
import org.anime_game_servers.multi_proto.gi.messages.general.ability.AbilityEmbryo;
import org.anime_game_servers.multi_proto.gi.messages.general.ability.AbilitySyncStateInfo;
import org.anime_game_servers.multi_proto.gi.messages.general.entity.SceneReliquaryInfo;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EntityAvatar extends GameEntity {
    @Getter private final Avatar avatar;

    @Getter private PlayerDieType killedType;
    @Getter private int killedBy;

    public EntityAvatar(Avatar avatar) {
        this(null, avatar);
    }

    public EntityAvatar(Scene scene, Avatar avatar) {
        super(scene);
        this.avatar = avatar;
        this.avatar.setCurrentEnergy();
        if (scene != null) {
            this.id = getScene().getWorld().getNextEntityId(EntityIdType.AVATAR);

            GameItem weapon = this.getAvatar().getWeapon();
            if (weapon != null) {
                if (!(weapon.getWeaponEntity() != null && weapon.getWeaponEntity().getScene() == scene)) {
                    weapon.setWeaponEntity(new EntityWeapon(getPlayer().getScene(), weapon.getItemData().getGadgetId()));
                    scene.getWeaponEntities().put(weapon.getWeaponEntity().getId(), weapon.getWeaponEntity());
                }
                //weapon.setWeaponEntityId(getScene().getWorld().getNextEntityId(EntityIdType.WEAPON));
            }
        }
    }

    @Override
    public int getEntityTypeId() {
        return getAvatar().getAvatarId();
    }

    public Player getPlayer() {
        return this.avatar.getPlayer();
    }

    @Override
    public Position getPosition() {
        return getPlayer().getPosition();
    }

    @Override
    public Position getRotation() {
        return getPlayer().getRotation();
    }

    @Override
    public boolean isAlive() {
        return this.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP) > 0f;
    }

    @Override
    public Int2FloatMap getFightProperties() {
        return getAvatar().getFightProperties();
    }

    public int getWeaponEntityId() {
        if (getAvatar().getWeapon() != null && getAvatar().getWeapon().getWeaponEntity() != null) {
            return getAvatar().getWeapon().getWeaponEntity().getId();
        }
        return 0;
    }

    @Override
    public void onDeath(int killerId) {
        super.onDeath(killerId); // Invoke super class's onDeath() method.

        this.killedType = PlayerDieType.PLAYER_DIE_KILL_BY_MONSTER;
        this.killedBy = killerId;
        clearEnergy(ChangeEnergyReason.CHANGE_ENERGY_NONE);
    }

    public void onDeath(PlayerDieType dieType, int killerId) {
        super.onDeath(killerId); // Invoke super class's onDeath() method.

        this.killedType = dieType;
        this.killedBy = killerId;
        clearEnergy(ChangeEnergyReason.CHANGE_ENERGY_NONE);
    }

    @Override
    public float heal(float amount, boolean mute) {
        // Do not heal character if they are dead
        if (!this.isAlive()) {
            return 0f;
        }

        float healed = super.heal(amount, mute);

        if (healed > 0f) {
            getScene().broadcastPacket(
                new PacketEntityFightPropChangeReasonNotify(this, FightProperty.FIGHT_PROP_CUR_HP, healed, mute ? PropChangeReason.PROP_CHANGE_NONE : PropChangeReason.PROP_CHANGE_ABILITY, ChangeHpReason.CHANGE_HP_ADD_ABILITY)
            );
        }

        return healed;
    }

    @Override
    public float heal(float amount) {
        return this.heal(amount, false);
    }

    public void clearEnergy(ChangeEnergyReason reason) {
        // Fight props.
        val curEnergyProp = this.getAvatar().getSkillDepot().getElementType().getCurEnergyProp();
        float curEnergy = this.getFightProperty(curEnergyProp);

        // Set energy to zero.
        this.avatar.setCurrentEnergy(curEnergyProp, 0);

        // Send packets.
        this.getScene().broadcastPacket(new PacketEntityFightPropUpdateNotify(this, curEnergyProp));

        if (reason == ChangeEnergyReason.CHANGE_ENERGY_SKILL_START) {
            this.getScene().broadcastPacket(new PacketEntityFightPropChangeReasonNotify(this, curEnergyProp, -curEnergy, reason));
        }
    }

    // for single entity avatar only I guess
    public boolean addEnergy(float amount) {
        FightProperty curEnergyProp = this.getAvatar().getSkillDepot().getElementType().getCurEnergyProp();
        float curEnergy = this.getFightProperty(curEnergyProp);
        if (curEnergy == amount) {
            return false;
        }
        this.getAvatar().setCurrentEnergy(curEnergyProp, amount);
        this.getScene().broadcastPacket(new PacketEntityFightPropUpdateNotify(this, curEnergyProp));
        return true;
    }

    public void addEnergy(float amount, PropChangeReason reason) {
        this.addEnergy(amount, reason, false);
    }

    public void addEnergy(float amount, PropChangeReason reason, boolean isFlat) {
        // Get current and maximum energy for this avatar.
        val elementType = this.getAvatar().getSkillDepot().getElementType();
        val curEnergyProp = elementType.getCurEnergyProp();
        val maxEnergyProp = elementType.getMaxEnergyProp();

        float curEnergy = this.getFightProperty(curEnergyProp);
        float maxEnergy = this.getFightProperty(maxEnergyProp);

        // Scale amount by energy recharge, if the amount is not flat.
        if (!isFlat) {
            amount *= this.getFightProperty(FightProperty.FIGHT_PROP_CHARGE_EFFICIENCY);
        }

        // Determine the new energy value.
        float newEnergy = Math.min(curEnergy + amount, maxEnergy);

        // Set energy and notify.
        if (newEnergy != curEnergy) {
            this.avatar.setCurrentEnergy(curEnergyProp, newEnergy);

            // I only see EntityFightPropUpdataNotify being sent by the official server, and without any prop reason,
            // not sure how that differs to this
            this.getScene().broadcastPacket(new PacketEntityFightPropChangeReasonNotify(this, curEnergyProp, newEnergy, reason));
        }
    }

    public SceneAvatarInfo getSceneAvatarInfo() {
        val avatar = this.getAvatar();
        val player = this.getPlayer();
        val avatarInfo = new SceneAvatarInfo(player.getUid(), avatar.getAvatarId(), avatar.getGuid(), player.getPeerId());
        avatarInfo.setTalentIdList(avatar.getTalentIdList().stream().toList());
        avatarInfo.setCoreProudSkillLevel(avatar.getCoreProudSkillLevel());
        avatarInfo.setSkillLevelMap(avatar.getSkillLevelMap());
        avatarInfo.setSkillDepotId(avatar.getSkillDepotId());
        avatarInfo.setInherentProudSkillList(avatar.getProudSkillList().stream().toList());
        avatarInfo.setProudSkillExtraLevelMap(avatar.getProudSkillBonusMap());
        avatarInfo.setTeamResonanceList(player.getTeamManager().getTeamResonances().stream().toList());
        avatarInfo.setWearingFlycloakId(avatar.getFlyCloak());
        avatarInfo.setCostumeId(avatar.getCostume());
        avatarInfo.setBornTime(avatar.getBornTime());

        val reliquaryList = new ArrayList<SceneReliquaryInfo>();
        val equipList = new ArrayList<Integer>();

        for (GameItem item : avatar.getEquips().values()) {
            if (item.getItemData().getEquipType() == EquipType.EQUIP_WEAPON) {
                avatarInfo.setWeapon(item.createSceneWeaponInfo());
            } else {
                reliquaryList.add(item.createSceneReliquaryInfo());
            }
            equipList.add(item.getItemId());
        }

        avatarInfo.setReliquaryList(reliquaryList);
        avatarInfo.setEquipIdList(equipList);

        return avatarInfo;
    }

    @Override
    public SceneEntityInfo toProto() {
        val bornPosProto = new org.anime_game_servers.multi_proto.gi.messages.general.Vector();
        val aiInfo = new SceneEntityAiInfo(true, bornPosProto);
        val authority = new EntityAuthorityInfo(new AbilitySyncStateInfo(), new EntityRendererChangedInfo(), aiInfo, bornPosProto);

        val entityInfo = new SceneEntityInfo(org.anime_game_servers.multi_proto.gi.messages.scene.entity.ProtEntityType.PROT_ENTITY_AVATAR, getId());
        entityInfo.setAnimatorParaList(List.of(new AnimatorParameterValueInfoPair()));
        entityInfo.setEntityClientData(new EntityClientData());
        entityInfo.setEntityAuthorityInfo(authority);
        entityInfo.setLastMoveSceneTimeMs(this.getLastMoveSceneTimeMs());
        entityInfo.setLastMoveReliableSeq(this.getLastMoveReliableSeq());
        entityInfo.setLifeState(this.getLifeState().getValue());

        if (this.getScene() != null) {
            entityInfo.setMotionInfo(this.getMotionInfo());
        }

        this.addAllFightPropsToEntityInfo(entityInfo);

        val pair = new org.anime_game_servers.multi_proto.gi.messages.scene.entity.PropPair(PlayerProperty.PROP_LEVEL.getId(), ProtoHelper.newPropValue(PlayerProperty.PROP_LEVEL, getAvatar().getLevel()));
        entityInfo.setPropList(List.of(pair));

        entityInfo.setEntity(new SceneEntityInfo.Entity.Avatar(this.getSceneAvatarInfo()));

        return entityInfo;
    }

    public AbilityControlBlock getAbilityControlBlock() {
        AvatarData data = this.getAvatar().getAvatarData();
        val abilityControlBlock = new AbilityControlBlock();
        val embryoId = new AtomicInteger(0);
        val embrioList = new ArrayList<AbilityEmbryo>();

        val abilities = data.getAbilities();
        // Add avatar abilities
        if (abilities != null) {
            embrioList.addAll(abilities.stream().map(id -> new AbilityEmbryo(embryoId.incrementAndGet(), id, GameConstants.DEFAULT_ABILITY_NAME)).toList());
        }
        // Add default abilities
        embrioList.addAll(Arrays.stream(GameConstants.DEFAULT_ABILITY_HASHES).mapToObj(id -> new AbilityEmbryo(embryoId.incrementAndGet(), id, GameConstants.DEFAULT_ABILITY_NAME)).toList());
        // Add team resonances
        embrioList.addAll(this.getPlayer().getTeamManager().getTeamResonancesConfig().stream().map(id -> new AbilityEmbryo(embryoId.incrementAndGet(), id, GameConstants.DEFAULT_ABILITY_NAME)).toList());
        // Add skill depot abilities
        AvatarSkillDepotData skillDepot = GameData.getAvatarSkillDepotDataMap().get(this.getAvatar().getSkillDepotId());
        if (skillDepot != null && skillDepot.getAbilities() != null) {
            embrioList.addAll(skillDepot.getAbilities().stream().map(id -> new AbilityEmbryo(embryoId.incrementAndGet(), id, GameConstants.DEFAULT_ABILITY_NAME)).toList());
        }
        // Add equip abilities
        if (this.getAvatar().getExtraAbilityEmbryos().size() > 0) {
            embrioList.addAll(this.getAvatar().getExtraAbilityEmbryos().stream().map(id -> new AbilityEmbryo(embryoId.incrementAndGet(), Utils.abilityHash(id), GameConstants.DEFAULT_ABILITY_NAME)).toList());
        }
        abilityControlBlock.setAbilityEmbryoList(embrioList);

        //
        return abilityControlBlock;
    }

    /**
     * Move this entity to a new position.
     * Additionally invoke player move event.
     *
     * @param newPosition The new position.
     * @param rotation    The new rotation.
     */
    @Override
    public void move(Position newPosition, Position rotation) {
        // Invoke player move event.
        PlayerMoveEvent event = new PlayerMoveEvent(
            this.getPlayer(), PlayerMoveEvent.MoveType.PLAYER,
            this.getPosition(), newPosition
        );
        event.call();

        // Set position and rotation.
        super.move(event.getDestination(), rotation);
    }
}
