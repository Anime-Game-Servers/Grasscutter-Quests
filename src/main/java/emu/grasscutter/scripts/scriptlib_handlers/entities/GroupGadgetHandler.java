package emu.grasscutter.scripts.scriptlib_handlers.entities;

import emu.grasscutter.Loggers;
import emu.grasscutter.game.entity.EntityBaseGadget;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nonnull;

import static org.anime_game_servers.gi_lua.script_lib.ScriptLibErrors.INVALID_PARAMETER;

public class GroupGadgetHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.entites.GroupGadgetHandler <GroupEventLuaContext>{
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    @Override
    public int ChangeGroupGadget(GroupEventLuaContext context, int configId, int state) {
        logger.debug("[LUA] Call ChangeGroupGadget with {} {}", configId, state);

        val scene = context.getSceneScriptManager().getScene();
        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        val entity = scene.getEntityByConfigId(configId, groupId);

        if(entity == null){
            return 1;
        }

        if (entity instanceof EntityGadget entityGadget) {
            entityGadget.updateState(state);
            return 0;
        }

        return 1;
    }

    @Override
    public int ExecuteGadgetLua(@Nonnull GroupEventLuaContext groupEventLuaContext, int groupId, int gadgetCfgId, int activityType, int var4, int var5) {
        return handleUnimplemented(groupId, gadgetCfgId, activityType, var4, var5);
    }

    @Override
    public float GetGadgetAbilityFloatValue(@NotNull GroupEventLuaContext context, int groupId, int configId, @NotNull String abilitySGVName) {
        return handleUnimplemented(groupId, configId, abilitySGVName);
    }

    @Override
    public int GetGadgetConfigId(@NotNull GroupEventLuaContext groupEventLuaContext, int gadgetEid) {
        logger.debug("[LUA] Call GetGadgetConfigId with {}",gadgetEid);
        val entity = groupEventLuaContext.getSceneScriptManager().getScene().getEntityById(gadgetEid);
        if(entity == null){
            return INVALID_PARAMETER.getValue();
        }
        return entity.getConfigId();
    }

    @Override
    public int GetGadgetHpPercent(@NotNull GroupEventLuaContext context, int groupId, int configId) {

        val scene = context.getSceneScriptManager().getScene();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val entity = scene.getEntityByConfigId(configId, actualGroupId);

        if(entity == null){
            return INVALID_PARAMETER.getValue();
        }
        if(entity.hasFightProperty(FightProperty.FIGHT_PROP_HP_PERCENT)){
            return (int) entity.getFightProperty(FightProperty.FIGHT_PROP_HP_PERCENT);
        }
        if(entity.hasFightProperty(FightProperty.FIGHT_PROP_CUR_HP) && entity.hasFightProperty(FightProperty.FIGHT_PROP_MAX_HP)){
            val curHp = entity.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP);
            val maxHp = entity.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP);
            return (int) (curHp*100/maxHp);
        }
        return -1;
    }

    @Override
    public int GetGadgetIdByEntityId(GroupEventLuaContext context, int entityId) {
        var entity = context.getSceneScriptManager().getScene().getEntityById(entityId);
        if(!(entity instanceof EntityBaseGadget)){
            return 0;
        }
        return ((EntityBaseGadget) entity).getGadgetId();
    }

    @Override
    public int GetGadgetStateByConfigId(GroupEventLuaContext context, int groupId, int configId) {
        logger.debug("[LUA] Call GetGadgetStateByConfigId with {},{}",
            groupId, configId);

        val scene = context.getSceneScriptManager().getScene();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val gadget = scene.getEntityByConfigId(configId, actualGroupId);

        if(!(gadget instanceof EntityGadget)){
            return -1;
        }
        return ((EntityGadget)gadget).getState();
    }

    @Override
    public int SetGadgetStateByConfigId(GroupEventLuaContext context, int configId, int gadgetState) {
        logger.debug("[LUA] Call SetGadgetStateByConfigId with {},{}",
            configId,gadgetState);

        val scene = context.getSceneScriptManager().getScene();
        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        val entity = scene.getEntityByConfigId(configId, groupId);

        if (!(entity instanceof EntityGadget)) {
            return 1;
        }

        ((EntityGadget) entity).updateState(gadgetState);
        return 0;
    }

    @Override
    public int SetGroupGadgetStateByConfigId(GroupEventLuaContext context, int groupId, int configId, int gadgetState) {
        logger.debug("[LUA] Call SetGroupGadgetStateByConfigId with {},{},{}",
            groupId,configId,gadgetState);

        val scene = context.getSceneScriptManager().getScene();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val entity = scene.getEntityByConfigId(configId, actualGroupId);

        if(!(entity instanceof EntityGadget)){
            return -1;
        }
        ((EntityGadget) entity).updateState(gadgetState);

        return 0;
    }
}
