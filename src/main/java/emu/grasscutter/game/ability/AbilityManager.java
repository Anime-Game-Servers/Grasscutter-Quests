package emu.grasscutter.game.ability;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.Loggers;
import emu.grasscutter.game.props.ElementReactionType;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.AbilityData;
import emu.grasscutter.data.binout.AbilityMixinData;
import emu.grasscutter.data.binout.AbilityModifier;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.actions.AbilityAction;
import emu.grasscutter.game.ability.actions.AbilityActionHandler;
import emu.grasscutter.game.ability.mixins.AbilityMixin;
import emu.grasscutter.game.ability.mixins.AbilityMixinHandler;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.player.BasePlayerManager;
import emu.grasscutter.game.player.Player;
import io.netty.util.concurrent.FastThreadLocalThread;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.ability.AbilityInvokeEntry;
import org.anime_game_servers.multi_proto.gi.messages.ability.ModifierAction;
import org.anime_game_servers.multi_proto.gi.messages.ability.meta.AbilityMetaAddAbility;
import org.anime_game_servers.multi_proto.gi.messages.ability.meta.AbilityMetaModifierChange;
import org.anime_game_servers.multi_proto.gi.messages.ability.meta.AbilityMetaReInitOverrideMap;
import org.anime_game_servers.multi_proto.gi.messages.ability.meta.AbilityMetaTriggerElementReaction;
import org.anime_game_servers.multi_proto.gi.messages.general.ability.AbilityScalarType;
import org.anime_game_servers.multi_proto.gi.messages.general.ability.AbilityScalarValueEntry;
import org.anime_game_servers.multi_proto.gi.messages.general.ability.AbilityString;
import org.slf4j.Logger;

import javax.annotation.Nullable;

public final class AbilityManager extends BasePlayerManager {
    @Getter
    private static final Logger logger = Loggers.getAbilitySystem();

    private final static HashMap<AbilityModifierAction.Type, AbilityActionHandler> actionHandlers = new HashMap<>();
    private final static HashMap<AbilityMixinData.Type, AbilityMixinHandler> mixinHandlers = new HashMap<>();

    public static final ExecutorService eventExecutor;
    static {
        eventExecutor = new ThreadPoolExecutor(4, 4,
            60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1000),
            FastThreadLocalThread::new, new ThreadPoolExecutor.AbortPolicy());

        registerHandlers();
    }

    @Getter private boolean abilityInvulnerable = false;

    public AbilityManager(Player player) {
        super(player);
    }

    public static void registerHandlers() {
        var handlerClassesAction = Grasscutter.reflector.getSubTypesOf(AbilityActionHandler.class);

        for (var obj : handlerClassesAction) {
            try {
                if (obj.isAnnotationPresent(AbilityAction.class)) {
                    AbilityModifierAction.Type abilityAction = obj.getAnnotation(AbilityAction.class).value();
                    actionHandlers.put(abilityAction, obj.getDeclaredConstructor().newInstance());
                } else {
                    return;
                }
            } catch (Exception e) {
                logger.error("Unable to register handler.", e);
            }
        }

        var handlerClassesMixin = Grasscutter.reflector.getSubTypesOf(AbilityMixinHandler.class);

        for (var obj : handlerClassesMixin) {
            try {
                if (obj.isAnnotationPresent(AbilityAction.class)) {
                    AbilityMixinData.Type abilityMixin = obj.getAnnotation(AbilityMixin.class).value();
                    mixinHandlers.put(abilityMixin, obj.getDeclaredConstructor().newInstance());
                } else {
                    return;
                }
            } catch (Exception e) {
                logger.error("Unable to register handler.", e);
            }
        }
    }

    public void executeAction(Ability ability, AbilityModifierAction action, byte[] abilityData, GameEntity target) {
        AbilityActionHandler handler = actionHandlers.get(action.type);

        if (handler == null || ability == null) {
            logger.error("Could not execute ability action {} at {}", action.type, ability);
            return;
        }

        eventExecutor.submit(() -> {
            if (!handler.execute(ability, action, abilityData, target)) {
                logger.error("exec ability action failed {} at {}", action.type, ability);
            }
        });
    }

    public void executeMixin(Ability ability, AbilityMixinData mixinData, byte[] abilityData) {
        AbilityMixinHandler handler = mixinHandlers.get(mixinData.type);

        if (handler == null || ability == null || mixinData == null) {
            logger.error("Could not execute ability mixin {} at {}", mixinData.type, ability);
            return;
        }

        eventExecutor.submit(() -> {
            if (!handler.execute(ability, mixinData, abilityData)) {
                logger.error("exec ability action failed {} at {}", mixinData.type, ability);
            }
        });
    }

    public void onAbilityInvoke(AbilityInvokeEntry invoke) throws Exception {
        logger.debug("Ability invoke: " + invoke + " " + invoke.getArgumentType() + " (" + invoke.getArgumentType() + "): " + this.player.getScene().getEntityById(invoke.getEntityId()));
        var entity = this.player.getScene().getEntityById(invoke.getEntityId());
        if(entity != null) {
            logger.debug("Entity group id {} config id {}", entity.getGroupId(), entity.getConfigId());
        }
        if(invoke.getHead() != null && invoke.getHead().getTargetId() != 0) {
            logger.debug("Target: " + this.player.getScene().getEntityById(invoke.getHead().getTargetId()));
        }

        if(invoke.getHead() != null && invoke.getHead().getLocalId() != 0) {
            this.handleServerInvoke(invoke);
            return;
        }

        switch (invoke.getArgumentType()) {
            case ABILITY_META_OVERRIDE_PARAM -> this.handleOverrideParam(invoke);
            case ABILITY_META_REINIT_OVERRIDEMAP -> this.handleReinitOverrideMap(invoke);
            case ABILITY_META_MODIFIER_CHANGE -> this.handleModifierChange(invoke);
            case ABILITY_MIXIN_COST_STAMINA -> this.handleMixinCostStamina(invoke);
            case ABILITY_ACTION_GENERATE_ELEM_BALL -> this.handleGenerateElemBall(invoke);
            case ABILITY_META_GLOBAL_FLOAT_VALUE -> this.handleGlobalFloatValue(invoke);
            case ABILITY_META_MODIFIER_DURABILITY_CHANGE -> this.handleModifierDurabilityChange(invoke);
            case ABILITY_META_ADD_NEW_ABILITY -> this.handleAddNewAbility(invoke);
            case ABILITY_META_TRIGGER_ELEMENT_REACTION -> this.handleTriggerElementReaction(invoke);
            default -> {}
        }
    }

    public void handleServerInvoke(AbilityInvokeEntry invoke) {
        var head = invoke.getHead();

        var entity = this.player.getScene().getEntityById(invoke.getEntityId());
        if(entity == null) {
            logger.info("Entity not found: {}", invoke.getEntityId());
            return;
        }

        var target = this.player.getScene().getEntityById(head.getTargetId());
        if(target == null) target = entity;

        Ability ability = null;

        //Seems that target is used, but need to be sure, TODO: Research

        //Find ability or modifier's ability
        if(head.getInstancedModifierId() != 0 && entity.getInstancedModifiers().containsKey(head.getInstancedModifierId())) {
            ability = entity.getInstancedModifiers().get(head.getInstancedModifierId()).getAbility();
        }

        if(ability == null && head.getInstancedAbilityId() != 0 && (head.getInstancedAbilityId() - 1) < entity.getInstancedAbilities().size()) {
            ability = entity.getInstancedAbilities().get(head.getInstancedAbilityId() - 1);
        }

        if(ability == null) {
            logger.warn("Ability not found: ability {} modifier {}", head.getInstancedAbilityId(), head.getInstancedModifierId());
            return;
        }

        //Time to reach the handlers
        var action = ability.getData().localIdToAction.get(head.getLocalId());
        if(action != null) {
            //Executing action

            executeAction(ability, action, invoke.getAbilityData(), target);

            return;
        } else {
            var mixin = ability.getData().localIdToMixin.get(head.getLocalId());

            if(mixin != null) {
                executeMixin(ability, mixin, invoke.getAbilityData());

                return;
            }
        }

        logger.info("Action or mixin not found: local_id {} ability {} actions to ids {}", head.getLocalId(), ability.getData().abilityName, ability.getData().localIdToAction.toString());
    }

    /**
     * Invoked when a player starts a skill.
     * @param player The player who started the skill.
     * @param skillId The skill ID.
     * @param casterId The caster ID.
     */
    public void onSkillStart(Player player, int skillId, int casterId) {
        // Check if the player matches this player.
        if (player.getUid() != this.player.getUid()) {
            return;
        }

        // Check if the caster matches the player.
        if (player.getTeamManager().getCurrentAvatarEntity().getId() != casterId) {
            return;
        }

        var skillData = GameData.getAvatarSkillDataMap().get(skillId);
        if (skillData == null) {
            return;
        }

        // Check if the skill is an elemental burst.
        if (skillData.getCostElemVal() <= 0) {
            return;
        }

        // Set the player as invulnerable.
        this.abilityInvulnerable = true;
    }

    /**
     * Invoked when a player ends a skill.
     * @param player The player who started the skill.
     */
    public void onSkillEnd(Player player) {
        // Check if the player matches this player.
        if (player.getUid() != this.player.getUid()) {
            return;
        }

        // Check if the player is invulnerable.
        if (!this.abilityInvulnerable) {
            return;
        }

        // Set the player as not invulnerable.
        this.abilityInvulnerable = false;
    }

    private void setAbilityOverrideValue(Ability ability, AbilityScalarValueEntry valueChange) {
        if(valueChange.getValueType() != AbilityScalarType.ABILITY_SCALAR_TYPE_FLOAT) {
            logger.info("Scalar type not supported: {}", valueChange.getValueType());

            return;
        }

        if(valueChange.getKey() == null){
            logger.warn("Key is null");
            return;
        }

        if(valueChange.getKey().getType() instanceof AbilityString.Type.Str str){
            val key = str.getValue();
            val value = ((AbilityScalarValueEntry.Value.FloatValue) valueChange.getValue()).getValue();

            ability.getAbilitySpecials().put(key, value);
            logger.debug("Ability {} changed {} to {}", ability.getData().abilityName, key, value);
        } else {
            // TODO
            logger.info("TODO: Calculate all the ability value hashes");
        }

    }

    private void handleOverrideParam(AbilityInvokeEntry invoke) throws Exception {
        var entity = this.player.getScene().getEntityById(invoke.getEntityId());
        var head = invoke.getHead();

        if(entity == null) {
            logger.info("Entity not found: {}", invoke.getEntityId());
            return;
        }

        var instancedAbilityIndex = head.getInstancedAbilityId() - 1;
        if(instancedAbilityIndex >= entity.getInstancedAbilities().size()) {
            logger.error("Ability not found {}", head.getInstancedAbilityId());
            return;
        }

        var valueChange = AbilityScalarValueEntry.parseBy(invoke.getAbilityData(), player.getSession().getVersion());

        var ability = entity.getInstancedAbilities().get(instancedAbilityIndex);
        setAbilityOverrideValue(ability, valueChange);
    }

    private void handleReinitOverrideMap(AbilityInvokeEntry invoke) throws Exception {
        var entity = this.player.getScene().getEntityById(invoke.getEntityId());
        var head = invoke.getHead();

        if(entity == null) {
            logger.info("Entity not found: {}", invoke.getEntityId());
            return;
        }

        var instancedAbilityIndex = head.getInstancedAbilityId() - 1;
        if(instancedAbilityIndex >= entity.getInstancedAbilities().size()) {
            logger.error("Ability not found {}", head.getInstancedAbilityId());
            return;
        }

        var ability = entity.getInstancedAbilities().get(instancedAbilityIndex);
        var valueChanges = AbilityMetaReInitOverrideMap.parseBy(invoke.getAbilityData(), player.getSession().getVersion());
        for (var variableChange : valueChanges.getOverrideMap()) {
            setAbilityOverrideValue(ability, variableChange);
        }
    }

    private void handleModifierChange(AbilityInvokeEntry invoke) throws Exception {
        //TODO:
        var modChange = AbilityMetaModifierChange.parseBy(invoke.getAbilityData(), player.getSession().getVersion());
        var head = invoke.getHead();

        if(head.getInstancedAbilityId() == 0 || head.getInstancedModifierId() > 2000) return; //Error: TODO: display error

        if(head.isServerbuffModifier()) {
            //TODO
            logger.info("TODO: Handle serverbuff modifier");

            return;
        }

        var entity = this.player.getScene().getEntityById(invoke.getEntityId());
        if(entity == null){
            logger.warn("Entity not found: {}", invoke.getEntityId());
            return;
        }

        if(modChange.getAction() == ModifierAction.ADDED) {
            AbilityData instancedAbilityData = null;
            Ability instancedAbility = null;

            if(head.getTargetId() != 0) {
                //Get ability from target entity
                var targetEntity = this.player.getScene().getEntityById(head.getTargetId());
                if(targetEntity != null) {
                    if((head.getInstancedAbilityId() - 1) < targetEntity.getInstancedAbilities().size()) {
                        instancedAbility = targetEntity.getInstancedAbilities().get(head.getInstancedAbilityId() - 1);
                        if(instancedAbility != null) instancedAbilityData = instancedAbility.getData();
                    }
                }
            }

            if(instancedAbilityData == null) {
                //search on entity base id
                if(entity != null) {
                    if((head.getInstancedAbilityId() - 1) < entity.getInstancedAbilities().size()) {
                        instancedAbility = entity.getInstancedAbilities().get(head.getInstancedAbilityId() - 1);
                        if(instancedAbility != null) instancedAbilityData = instancedAbility.getData();
                    }
                }
            }

            if(instancedAbilityData == null) {
                //Search for the parent ability

                //TODO: Research about hash

                val abilityName = modChange.getParentAbilityName();
                if(abilityName != null) {
                    instancedAbilityData = GameData.getAbilityData(getAbilityName(abilityName));
                }
            }

            if(instancedAbilityData == null ||  instancedAbilityData.modifiers == null) {
                logger.info("No ability found");
                return; //TODO: Display error message
            }

            var modifierArray = instancedAbilityData.modifiers.values().toArray();
            if(modChange.getModifierLocalId() >= modifierArray.length)
            {
                logger.info("Modifier local id {} not found", modChange.getModifierLocalId());
                return;
            }
            var modifierData = (AbilityModifier)modifierArray[modChange.getModifierLocalId()];
            if(entity.getInstancedModifiers().containsKey(head.getInstancedModifierId())) {
                logger.debug("Replacing entity {} modifier id {} with ability {} modifier {}", invoke.getEntityId(), head.getInstancedModifierId(), instancedAbilityData.abilityName, modifierData);
            } else {
                logger.debug("Adding entity {} modifier id {} with ability {} modifier {}", invoke.getEntityId(), head.getInstancedModifierId(), instancedAbilityData.abilityName, modifierData);
            }

            AbilityModifierController modifier = new AbilityModifierController(instancedAbility, instancedAbilityData, modifierData);

            entity.getInstancedModifiers().put(head.getInstancedModifierId(), modifier);

            //TODO: Add all the ability modifier property change
        } else if(modChange.getAction() == ModifierAction.REMOVED) {
            logger.debug("Removed on entity {} modifier id {}: {}", invoke.getEntityId(), head.getInstancedModifierId(), entity.getInstancedModifiers().get(head.getInstancedModifierId()));

            //TODO: Add debug log

            entity.getInstancedModifiers().remove(head.getInstancedModifierId());
        } else {
            //TODO: Display error message
            logger.debug("Unknown action");
        }
    }

    private void handleMixinCostStamina(AbilityInvokeEntry invoke) {

    }

    private void handleGenerateElemBall(AbilityInvokeEntry invoke) {

    }

    @Nullable
    private String getAbilityName(AbilityString protoString) {
        if(protoString.getType() instanceof AbilityString.Type.Str str){
            return str.getValue();
        } else if (protoString.getType() instanceof AbilityString.Type.Hash hash){
            return GameData.getAbilityHashes().get((int)hash.getValue());
        }
        return null;
    }

    private void handleGlobalFloatValue(AbilityInvokeEntry invoke) {
        var entity = this.player.getScene().getEntityById(invoke.getEntityId());
        if(entity == null) return;

        var entry = AbilityScalarValueEntry.parseBy(invoke.getAbilityData(), player.getSession().getVersion());
        if(entry == null || !(entry.getValue() instanceof AbilityScalarValueEntry.Value.FloatValue)) return;

        String key = getAbilityName(entry.getKey());

        if(key == null) return;

        if(key.startsWith("SGV_")) return; //Server does not allow to change this variables I think
        switch(entry.getValueType()) {
            case ABILITY_SCALAR_TYPE_FLOAT: {
                float value = ((AbilityScalarValueEntry.Value.FloatValue) entry.getValue()).getValue();
                if (!Float.isNaN(value))
                    entity.getGlobalAbilityValues().put(key, value);
                break;
            }
            case ABILITY_SCALAR_TYPE_UINT:
                float uvalue = ((AbilityScalarValueEntry.Value.UintValue) entry.getValue()).getValue();
                entity.getGlobalAbilityValues().put(key, uvalue);
                break;

            default:
                return;
        }
    }

    private void invokeAction(AbilityModifierAction action, GameEntity target, GameEntity sourceEntity) {

    }

    private void handleModifierDurabilityChange(AbilityInvokeEntry invoke) {

    }

    private void handleAddNewAbility(AbilityInvokeEntry invoke) {
        var entity = this.player.getScene().getEntityById(invoke.getEntityId());

        if(entity == null) {
            logger.info("Entity not found: {}", invoke.getEntityId());
            return;
        }

        var addAbility = AbilityMetaAddAbility.parseBy(invoke.getAbilityData(), player.getSession().getVersion());

        var abilityName = Ability.getAbilityName(addAbility.getAbility().getAbilityName());

        var ability = GameData.getAbilityData(abilityName);
        if(ability == null) {
            logger.info("Ability not found: {}", abilityName);
            return;
        }

        entity.getInstancedAbilities().add(new Ability(ability, entity, player));

        logger.debug("Ability added to entity {} at index {}", entity.getId(), entity.getInstancedAbilities().size());
    }

    /**
     * Invoked when an entity triggered an elemental reaction.
     * @param invoke Holds information of elemental reaction, attacker and target.
     */
    private void handleTriggerElementReaction(AbilityInvokeEntry invoke) {
        if (getPlayer().getScene() == null) return;

        AbilityMetaTriggerElementReaction data = AbilityMetaTriggerElementReaction.parseBy(invoke.getAbilityData(), player.getSession().getVersion());
        GameEntity targetEntity = getPlayer().getScene().getEntityById(invoke.getEntityId());
        if (targetEntity == null) return;

        ElementReactionType reactionType = ElementReactionType.getTypeByValue(data.getElementReactionType());
        // Challenge related action
        Optional.ofNullable(getPlayer().getScene().getChallenge())
            .ifPresent(sceneChallenge -> sceneChallenge.onElementReaction(targetEntity, reactionType));
    }

    public void addAbilityToEntity(GameEntity entity, String name) {
        AbilityData data = GameData.getAbilityData(name);
        if(data != null)
            addAbilityToEntity(entity, data);
    }

    public void addAbilityToEntity(GameEntity entity, AbilityData abilityData) {
        Ability ability = new Ability(abilityData, entity, this.player);
        entity.getInstancedAbilities().add(ability); //This are in order
    }
}

