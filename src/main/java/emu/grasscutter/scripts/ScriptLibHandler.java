package emu.grasscutter.scripts;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.game.activity.ActivityManager;
import emu.grasscutter.game.dungeons.challenge.ChallengeInfo;
import emu.grasscutter.game.dungeons.challenge.ChallengeScoreInfo;
import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import emu.grasscutter.game.dungeons.challenge.factory.ChallengeFactory;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.entity.EntityMonster;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.entity.gadget.GadgetWorktop;
import emu.grasscutter.game.entity.gadget.platform.ConfigRoute;
import emu.grasscutter.game.entity.gadget.platform.PointArrayRoute;
import emu.grasscutter.game.managers.blossom.BlossomSchedule;
import emu.grasscutter.game.managers.blossom.enums.BlossomRefreshType;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.enums.QuestCond;
import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.game.world.SceneGroupInstance;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import emu.grasscutter.server.packet.send.*;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.core.gi.enums.QuestState;
import org.anime_game_servers.core.gi.models.Vector;
import org.anime_game_servers.gi_lua.models.ScriptArgs;
import org.anime_game_servers.gi_lua.models.constants.*;
import org.anime_game_servers.gi_lua.models.constants.temporary.GalleryProgressScoreType;
import org.anime_game_servers.gi_lua.models.constants.temporary.GalleryProgressScoreUIType;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;
import org.anime_game_servers.gi_lua.models.scene.group.SceneObject;
import org.anime_game_servers.gi_lua.script_lib.ActivityOpenAndCloseTime;
import org.anime_game_servers.gi_lua.script_lib.LuaContext;
import org.anime_game_servers.gi_lua.script_lib.SealBattleParams;
import org.anime_game_servers.gi_lua.script_lib.handler.parameter.KillByConfigIdParams;
import org.anime_game_servers.lua.engine.LuaTable;
import org.anime_game_servers.multi_proto.gi.messages.scene.EnterType;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

import java.util.*;

import static emu.grasscutter.game.props.EnterReason.Lua;
import static org.anime_game_servers.gi_lua.models.constants.GroupKillPolicy.*;
import static org.anime_game_servers.gi_lua.utils.ScriptUtils.luaToPos;

public class ScriptLibHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.ScriptLibHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = BaseHandler.getLogger();

    private GameEntity createGadget(SceneScriptManager sceneScriptManager, int configId, org.anime_game_servers.gi_lua.models.scene.group.SceneGroup group){
        val groupId = group.getGroupInfo().getId();
        val groupBlockId = group.getGroupInfo().getBlockId();
        val groupGadgets = group.getGadgets();
        if (groupGadgets == null){
            logger.warn("[LUA] Create gadget called with cid: {} gid: {} bid: {}, but gadgets is null", configId, groupId, groupBlockId);
            return null;
        }

        val gadget = groupGadgets.get(configId);
        if (gadget == null){
            logger.warn("[LUA] Create gadget called with cid: {} gid: {} bid: {}, but gadget is null", configId, groupId, groupBlockId);
            return null;
        }

        val entity = sceneScriptManager.createGadget(groupId, groupBlockId, gadget);
        if(entity==null){
            logger.warn("[LUA] Create gadget null with cid: {} gid: {} bid: {}", configId, groupId, groupBlockId);
            return null;
        }

        sceneScriptManager.addEntity(entity);
        return entity;
    }


    private String printTable(LuaTable table){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(var meta : table.getKeys()){
            sb.append(meta).append(":").append(table.get(meta)).append(",");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public void PrintGroupWarning(LuaContext luaContext, String msg) {
        if(luaContext instanceof emu.grasscutter.scripts.lua_engine.GroupEventLuaContext){
            var group = ((emu.grasscutter.scripts.lua_engine.GroupEventLuaContext) luaContext).getCurrentGroup();
            logger.debug("[LUA] PrintContextLog {} {}", group.getGroupInfo().getId(), msg);
            return;
        } else {
            logger.debug("[LUA] PrintContextLog {}", msg);
        }
    }

    @Override
    public int SetGadgetEnableInteract(GroupEventLuaContext context, int groupId, int configId, boolean enable) {

        val scene = context.getSceneScriptManager().getScene();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val entity = scene.getEntityByConfigId(configId, actualGroupId);
        if(entity == null) return -1;

        if((entity instanceof EntityGadget gadget)){
            gadget.setInteractEnabled(enable);
            return 0;
        }

        return -2;
    }

    @Override
    public int SetWorktopOptionsByGroupId(GroupEventLuaContext context, int groupId, int configId, LuaTable options) {
        logger.debug("[LUA] Call SetWorktopOptionsByGroupId with {},{},{}",
            groupId,configId,printTable(options));

        val scene = context.getSceneScriptManager().getScene();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val entity = scene.getEntityByConfigId(configId, actualGroupId);

        if (!(entity instanceof EntityGadget gadget)) {
            return 1;
        }

        if (!(gadget.getContent() instanceof GadgetWorktop worktop)) {
            return 2;
        }

        worktop.addWorktopOptions(options.getAsIntArray());
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketWorktopOptionNotify(gadget));

        return 0;
    }

    @Override
    public int SetWorktopOptions(GroupEventLuaContext context, LuaTable table) {
        logger.debug("[LUA] Call SetWorktopOptions with {}", printTable(table));
        val callParams = context.getArgs();
        val group = context.getCurrentGroup();
        val scene = context.getSceneScriptManager().getScene();
        if(callParams == null || group == null){
            return 1;
        }
        val eid = callParams.getSourceEntityId();
        val entity = scene.getEntityById(eid);


        int[] worktopOptions = table.getAsIntArray();
        if(!(entity instanceof EntityGadget gadget)|| worktopOptions.length == 0){
            return 2;
        }

        if (!(gadget.getContent() instanceof GadgetWorktop worktop)) {
            return 3;
        }

        worktop.addWorktopOptions(worktopOptions);
        Grasscutter.getGameServer().getScheduler().scheduleDelayedTask(() -> {
            scene.broadcastPacket(new PacketWorktopOptionNotify(gadget));
        },1);
        return 0;
    }

    @Override
    public int DelWorktopOptionByGroupId(GroupEventLuaContext context, int groupId, int configId, int option) {
        logger.debug("[LUA] Call DelWorktopOptionByGroupId with {},{},{}",groupId,configId,option);

        val scene = context.getSceneScriptManager().getScene();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val entity = scene.getEntityByConfigId(configId, actualGroupId);

        if (!(entity instanceof EntityGadget gadget)) {
            return 1;
        }

        if (!(gadget.getContent() instanceof GadgetWorktop worktop)) {
            return 1;
        }

        worktop.removeWorktopOption(option);
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketWorktopOptionNotify(gadget));

        return 0;
    }

    @Override
    public int DelWorktopOption(GroupEventLuaContext context, int var1) {
        logger.debug("[LUA] Call DelWorktopOption with {}", var1);
        val callParams = context.getArgs();
        val group = context.getCurrentGroup();
        val scene = context.getSceneScriptManager().getScene();
        if(callParams == null || group == null){
            return 1;
        }
        var eid = callParams.getSourceEntityId();
        var entity = scene.getEntityById(eid);
        if (!(entity instanceof EntityGadget gadget)) {
            return 1;
        }

        if (!(gadget.getContent() instanceof GadgetWorktop worktop)) {
            return 2;
        }

        worktop.removeWorktopOption(callParams.getParam2());

        Grasscutter.getGameServer().getScheduler().scheduleDelayedTask(() -> {
            scene.broadcastPacket(new PacketWorktopOptionNotify(gadget));
        },1);

        return 0;
    }

    @Override
    public int AutoMonsterTide(GroupEventLuaContext context, int challengeIndex, int groupId, Integer[] ordersConfigId, int tideCount, int sceneLimit, int param6) {
        logger.debug("[LUA] Call AutoMonsterTide with {},{},{},{},{},{}",
            challengeIndex,groupId,ordersConfigId,tideCount,sceneLimit,param6);

        val group = getGroupOrCurrent(context, groupId);

        if (group == null || group.getMonsters() == null) {
            return 1;
        }

        context.getSceneScriptManager().startMonsterTideInGroup(challengeIndex, group, ordersConfigId, tideCount, sceneLimit);

        return 0;
    }

    @Override
    public int GoToGroupSuite(GroupEventLuaContext context, int groupId, int suite) {
        logger.debug("[LUA] Call GoToGroupSuite with {},{}",
            groupId,suite);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val scriptManager = context.getSceneScriptManager();
        SceneGroup group = getGroupOrCurrent(context, groupId);
        SceneGroupInstance groupInstance = scriptManager.getGroupInstanceById(actualGroupId);
        if (group == null || groupInstance == null) {
            return 1;
        }
        var suiteData = group.getSuiteByIndex(suite);
        if(suiteData == null){
            return 1;
        }

		/*for(var suiteItem : group.suites){
			if(suiteData == suiteItem){
				continue;
			}
			this.getSceneScriptManager().removeGroupSuite(group, suiteItem);
		}*/
        if(groupInstance.getActiveSuiteId() == 0 || groupInstance.getActiveSuiteId() != suite) {
            groupInstance.getDeadEntities().clear();
            scriptManager.addGroupSuite(groupInstance, suiteData);
            groupInstance.setActiveSuiteId(suite);
        }

        return 0;
    }

    @Override
    public int AddExtraGroupSuite(GroupEventLuaContext context, int groupId, int suite) {
        logger.debug("[LUA] Call AddExtraGroupSuite with {},{}",
            groupId,suite);
        val scriptManager = context.getSceneScriptManager();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        SceneGroup group = getGroupOrCurrent(context, groupId);
        if (group == null) {
            return 1;
        }

        SceneGroupInstance groupInstance = scriptManager.getGroupInstanceById(actualGroupId);
        if (groupInstance == null) {
            return 1;
        }

        var suiteData = group.getSuiteByIndex(suite);
        if(suiteData == null){
            logger.warn("trying to get suite that doesn't exist: {} {}", actualGroupId, suite);
            return 1;
        }
        scriptManager.addGroupSuite(groupInstance, suiteData);

        return 0;
    }

    @Override
    public int RemoveExtraGroupSuite(GroupEventLuaContext context, int groupId, int suite) {
        logger.debug("[LUA] Call RemoveExtraGroupSuite with {},{}",
            groupId,suite);

        SceneGroup group = getGroupOrCurrent(context, groupId);
        if (group == null) {
            return 1;
        }
        var suiteData = group.getSuiteByIndex(suite);
        if(suiteData == null){
            return 1;
        }

        context.getSceneScriptManager().removeGroupSuite(group, suiteData);

        return 0;
    }

    @Override
    public int KillExtraGroupSuite(GroupEventLuaContext context, int groupId, int suite) {
        logger.debug("[LUA] Call KillExtraGroupSuite with {},{}",
            groupId,suite);

        SceneGroup group = getGroupOrCurrent(context, groupId);
        if (group == null) {
            return 1;
        }
        var suiteData = group.getSuiteByIndex(suite);
        if(suiteData == null){
            return 1;
        }

        context.getSceneScriptManager().killGroupSuite(group, suiteData);

        return 0;
    }

    @Override
    public int AddExtraFlowSuite(GroupEventLuaContext context, int groupId, int suiteId, FlowSuiteOperatePolicy flowSuitePolicy) {
        return handleUnimplemented(groupId, suiteId, flowSuitePolicy.name());
    }

    @Override
    public int RemoveExtraFlowSuite(GroupEventLuaContext context, int groupId, int suiteId, FlowSuiteOperatePolicy flowSuitePolicy) {
        return handleUnimplemented(groupId, suiteId, flowSuitePolicy.name(), flowSuitePolicy);
    }

    @Override
    public int KillExtraFlowSuite(GroupEventLuaContext context, int groupId, int suiteId, FlowSuiteOperatePolicy flowSuitePolicy) {
        return handleUnimplemented(groupId, suiteId, flowSuitePolicy.name(), flowSuitePolicy);
    }

    @Override
    public int ActiveChallenge(GroupEventLuaContext context, int challengeIndex, int challengeId, int timeLimitOrGroupId, int groupId, int objectiveKills, int param5) {
        logger.debug("[LUA] Call ActiveChallenge with {},{},{},{},{},{}",
            challengeIndex, challengeId, timeLimitOrGroupId,groupId,objectiveKills,param5);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val challenge = ChallengeFactory.getChallenge(
            new ChallengeInfo(challengeIndex, challengeId, 0),
            List.of(timeLimitOrGroupId, actualGroupId, objectiveKills, param5),
            new ChallengeScoreInfo(0, 0),
            context.getSceneScriptManager().getScene(),
            context.getCurrentGroup()
        );

        if(challenge == null) return 1;

        context.getSceneScriptManager().getScene().setChallenge(challenge);
        challenge.start();
        return 0;
    }

    @Override
    public int StartChallenge(GroupEventLuaContext context, int challengeIndex, int challengeId, LuaTable challengeParams) {
        logger.info("[LUA] Call StartChallenge with {},{},{}", challengeIndex, challengeId, challengeParams);
        val challenge = ChallengeFactory.getChallenge(
            new ChallengeInfo(challengeIndex, challengeId, 0),
            Arrays.stream(challengeParams.getAsIntArray()).boxed().toList(),
            new ChallengeScoreInfo(0, 0),
            context.getSceneScriptManager().getScene(),
            context.getCurrentGroup()
        );

        if(challenge == null) return 1;

        context.getSceneScriptManager().getScene().setChallenge(challenge);
        challenge.start();
        return 0;
    }

    @Override
    public int StopChallenge(GroupEventLuaContext context, int challengeIndex, int result) {
        logger.debug("[LUA] Call StopChallenge with ");
        var challenge = context.getSceneScriptManager().getScene().getChallenge();
        if(challenge == null){
            return 1;
        }
        if(challenge.getInfo().getChallengeIndex() != challengeIndex){
            return 2;
        }

        switch (result) {
            case 0 -> challenge.fail();
            case 1 -> challenge.done();
            default -> {
                logger.warn("[LUA] Call StopChallenge with unsupported result {}", result);
                return 3;
            }
        }
        return 0;
    }

    @Override
    public int AddChallengeDuration(GroupEventLuaContext context, int challengeId, int duration) {
        logger.warn("[LUA] Call unimplemented AddChallengeDuration with {},{}", challengeId, duration);
        var challenge = context.getSceneScriptManager().getScene().getChallenge();
        if(challenge == null){
            return 1;
        }
        if(challenge.getInfo().getChallengeId() != challengeId){
            return 2;
        }
        /*if(!challenge.addDuration(duration)){
            return 3;
        }*/
        return 0;
    }

    @Override
    public int GetGroupMonsterCountByGroupId(GroupEventLuaContext context, int groupId) {
        logger.debug("[LUA] Call GetGroupMonsterCountByGroupId with {}",
            groupId);
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        return (int) context.getSceneScriptManager().getScene().getEntities().values().stream()
            .filter(e -> e instanceof EntityMonster && e.getGroupId() == actualGroupId)
            .count();
    }

    @Override
    public int CreateVariable(GroupEventLuaContext context, String type, LuaTable value) {
        logger.warn("[LUA] Call unimplemented CreateVariable with {} {}",
            type, value);
        //TODO implement
        switch (type){
            case "int":
            default:
                logger.warn("[LUA] Call CreateVariable with unsupported type {} and value {}", type, value);
        }
        return 0;
    }

    @Override
    public int SetVariableValue(GroupEventLuaContext context, int var1) {
        return handleUnimplemented(var1);
    }

    @Override
    public int GetVariableValue(GroupEventLuaContext context, int var1) {
        return handleUnimplemented(var1);
    }

    private static int getGroupVariableValue(SceneScriptManager sceneScriptManager, int groupId, String varName){
        return sceneScriptManager.getVariables(groupId).getOrDefault(varName, 0);
    }

    private static int modifyGroupVariableValue(SceneScriptManager sceneScriptManager, int groupId, String varName, int value,
                                                boolean isSet){
        val variables = sceneScriptManager.getVariables(groupId);

        val old = variables.getOrDefault(varName, value);
        val newValue = isSet ? value : old + value;
        variables.put(varName, newValue);
        sceneScriptManager.callEvent(
            new ScriptArgs(groupId, EventType.EVENT_VARIABLE_CHANGE, newValue, old)
                .setEventSource(varName)
        );
        return 0;
    }

    @Override
    public int GetGroupVariableValue(GroupEventLuaContext context, String var) {
        logger.debug("[LUA] Call GetGroupVariableValue with {}",
            var);

        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        return getGroupVariableValue(context.getSceneScriptManager(), groupId, var);
    }

    @Override
    public int GetGroupVariableValueByGroup(GroupEventLuaContext context, String name, int groupId) {
        logger.debug("[LUA] Call GetGroupVariableValueByGroup with {},{}",
            name,groupId);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        return getGroupVariableValue(context.getSceneScriptManager(), actualGroupId, name);
    }

    @Override
    public int SetGroupVariableValue(GroupEventLuaContext context, String varName, int value) {
        logger.debug("[LUA] Call SetGroupVariableValue with {},{}",
            varName, value);

        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        return modifyGroupVariableValue(context.getSceneScriptManager(), groupId, varName, value, true);
    }

    @Override
    public int SetGroupVariableValueByGroup(GroupEventLuaContext context, String key, int value, int groupId) {
        logger.debug("[LUA] Call SetGroupVariableValueByGroup with {},{},{}",
            key,value,groupId);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        return modifyGroupVariableValue(context.getSceneScriptManager(), actualGroupId, key, value, true);
    }

    @Override
    public int ChangeGroupVariableValue(GroupEventLuaContext context, String varName, int value) {
        logger.debug("[LUA] Call ChangeGroupVariableValue with {},{}",
            varName, value);

        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        return modifyGroupVariableValue(context.getSceneScriptManager(), groupId, varName, value, false);
    }

    @Override
    public int ChangeGroupVariableValueByGroup(GroupEventLuaContext context, String name, int value, int groupId) {
        logger.debug("[LUA] Call ChangeGroupVariableValueByGroup with {},{}",
            name,groupId);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        return modifyGroupVariableValue(context.getSceneScriptManager(), actualGroupId, name, value, false);
    }

    @Override
    public int RefreshGroup(GroupEventLuaContext context, LuaTable table) {
        logger.debug("[LUA] Call RefreshGroup with {}",
            printTable(table));
        // Kill and Respawn?
        val groupId = getGroupIdOrCurrentId(context, table.getInt("group_id"));
        val suite = table.getInt("suite");

        SceneGroupInstance groupInstance = context.getSceneScriptManager().getGroupInstanceById(groupId);

        if (groupInstance == null) {
            logger.warn("[LUA] trying to refresh unloaded group {}", groupId);
            return 1;
        }

        context.getSceneScriptManager().refreshGroup(groupInstance, suite, false);

        return 0;
    }

    @Override
    public int GetRegionEntityCount(GroupEventLuaContext context, int regionEid, EntityType entityType) {
        logger.debug("[LUA] Call GetRegionEntityCount with {} {}", regionEid, entityType.name());

        var region = context.getSceneScriptManager().getRegionById(regionEid);

        if (region == null) {
            return 0;
        }

        return (int) region.getEntities().stream().filter(e -> e.getEntityType().name().toUpperCase().equals(entityType.name())).count();
    }

    @Override
    public int GetRegionConfigId(GroupEventLuaContext context, int regionEid) {
        logger.debug("[LUA] Call GetRegionConfigId with {}", regionEid);
        val region = context.getSceneScriptManager().getRegionById(regionEid);
        if (region == null){
            return -1;
        }
        return region.getConfigId();
    }

    @Override
    public int TowerCountTimeStatus(GroupEventLuaContext context, int isDone, int var2) {
        // TODO record timer
        return handleUnimplemented(isDone, var2);
    }

    @Override
    public int GetGroupMonsterCount(GroupEventLuaContext context) {
        logger.debug("[LUA] Call GetGroupMonsterCount ");

        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        return (int) context.getSceneScriptManager().getScene().getEntities().values().stream()
            .filter(e -> e instanceof EntityMonster &&
                e.getGroupId() == groupId)
            .count();
    }

    @Override
    public int SetMonsterBattleByGroup(GroupEventLuaContext context, int configId, int groupId) {
        logger.debug("[LUA] Call SetMonsterBattleByGroup with {} {}",
            configId,groupId);
        // TODO implement scene50008_group250008057.lua uses incomplete group numbers

        val scene = context.getSceneScriptManager().getScene();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        // -> MonsterForceAlertNotify
        var entity = scene.getEntityByConfigId(configId, actualGroupId);
        if(entity != null && entity instanceof EntityMonster monster) {
            scene.broadcastPacket(new PacketMonsterForceAlertNotify(monster.getId()));
        }

        return 0;
    }

    @Override
    public int CauseDungeonFail(GroupEventLuaContext context) {
        logger.debug("[LUA] Call CauseDungeonFail with");

        var scriptManager = context.getSceneScriptManager();
        if(scriptManager==null){
            return 1;
        }

        var dungeonManager = scriptManager.getScene().getDungeonManager();
        if(dungeonManager==null){
            return 2;
        }

        dungeonManager.failDungeon();
        return 0;
    }

    @Override
    public int CauseDungeonSuccess(GroupEventLuaContext context) {
        logger.debug("[LUA] Call CauseDungeonSuccess with");

        var scriptManager = context.getSceneScriptManager();
        if(scriptManager==null){
            return 1;
        }

        var dungeonManager = scriptManager.getScene().getDungeonManager();
        if(dungeonManager==null){
            return 2;
        }

        dungeonManager.finishDungeon();
        return 0;
    }

    @Override
    public int SetEntityServerGlobalValueByConfigId(GroupEventLuaContext context, int cfgId, String sgvName, int value) {
        return handleUnimplemented(cfgId, sgvName, value);
    }

    @Override
    public int SetGroupLogicStateValue(GroupEventLuaContext context, String sgvName, int value) {
        return handleUnimplemented(sgvName, value);
    }

    @Override
    public int SetIsAllowUseSkill(GroupEventLuaContext context, int canUse) {
        logger.debug("[LUA] Call SetIsAllowUseSkill with {}",
            canUse);

        context.getSceneScriptManager().getScene().broadcastPacket(new PacketCanUseSkillNotify(canUse == 1));
        return 0;
    }

    @Override
    public int KillEntityByConfigId(GroupEventLuaContext groupEventLuaContext, KillByConfigIdParams killByConfigIdParams) {
        logger.debug("[LUA] Call KillEntityByConfigId with {}", killByConfigIdParams);
        SceneScriptManager scriptManager = groupEventLuaContext.getSceneScriptManager();

        int groupId = getGroupIdOrCurrentId(groupEventLuaContext, killByConfigIdParams.getGroupId());
        var entity = scriptManager.getScene().getEntityByConfigId(killByConfigIdParams.getConfigId(), groupId);
        if (entity == null) {
            return 0;
        }
        scriptManager.getScene().killEntity(entity, 0);
        return 0;
    }

    @Override
    public int CreateMonster(GroupEventLuaContext context, LuaTable table) {
        logger.debug("[LUA] Call CreateMonster with {}",
            printTable(table));
        val configId = table.getInt("config_id");
        val delayTime = table.getInt("delay_time");
        val group = context.getCurrentGroup();

        context.getSceneScriptManager().spawnMonstersByConfigId(group, configId, delayTime);
        return 0;
    }

    @Override
    public int TowerMirrorTeamSetUp(GroupEventLuaContext context, int team, int var1) {
        logger.debug("[LUA] Call TowerMirrorTeamSetUp with {},{}",
            team,var1);

        context.getSceneScriptManager().unloadCurrentMonsterTide();
        context.getSceneScriptManager().getScene().getPlayers().get(0).getTowerManager().mirrorTeamSetUp(team-1);

        return 0;
    }

    @Override
    public int CreateGadget(GroupEventLuaContext context, LuaTable table) {
        logger.debug("[LUA] Call CreateGadget with {}",
            printTable(table));
        val configId = table.getInt("config_id");
        val group = context.getCurrentGroup();

        createGadget(context.getSceneScriptManager(), configId, group);
        return 0;
    }

    @Override
    public int CreateGadgetByConfigIdByPos(GroupEventLuaContext groupEventLuaContext, int i, Vector position, Vector position1) {
        return 0;
    }

    @Override
    public int CreateGadgetByParamTable(GroupEventLuaContext groupEventLuaContext, LuaTable luaTable) {
        return 0;
    }

    @Override
    public int CreateVehicle(GroupEventLuaContext groupEventLuaContext, int i, int i1, Vector position, Vector position1) {
        return 0;
    }

    @Override
    public int CheckRemainGadgetCountByGroupId(GroupEventLuaContext context, LuaTable table) {
        logger.debug("[LUA] Call CheckRemainGadgetCountByGroupId with {}",
            printTable(table));
        val actualGroupId = getGroupIdOrCurrentId(context, table.getInt("group_id"));

        var count = context.getSceneScriptManager().getScene().getEntities().values().stream()
            .filter(g -> g instanceof EntityGadget entityGadget && entityGadget.getGroupId() == actualGroupId)
            .count();
        return (int)count;
    }

    @Override
    public int MarkPlayerAction(GroupEventLuaContext context, int var1, int var2, int var3) {
        logger.debug("[LUA] Call MarkPlayerAction with {},{},{}",
            var1, var2,var3);

        return 0;
    }

    @Override
    public int AddQuestProgress(GroupEventLuaContext context, String eventNotifyName) {
        logger.debug("[LUA] Call AddQuestProgress with {}",
            eventNotifyName);

        for(var player : context.getSceneScriptManager().getScene().getPlayers()){
            player.getQuestManager().queueEvent(QuestCond.QUEST_COND_LUA_NOTIFY, eventNotifyName);
            player.getQuestManager().queueEvent(QuestContent.QUEST_CONTENT_LUA_NOTIFY, eventNotifyName);
        }

        return 0;
    }

    @Override
    public int GetSceneOwnerUid(GroupEventLuaContext context) {
        return context.getSceneScriptManager().getScene().getWorld().getHost().getUid();
    }

    @Override
    public QuestState GetHostQuestState(GroupEventLuaContext context, int questId) {
        val player = context.getSceneScriptManager().getScene().getWorld().getHost();

        val quest = player.getQuestManager().getQuestById(questId);
        if(quest == null){
            return QuestState.QUEST_STATE_NONE;
        }

        return quest.getState();
    }

    @Override
    public QuestState GetQuestState(GroupEventLuaContext context, int entityId, int questId) {
        val player = context.getSceneScriptManager().getScene().getWorld().getHost();

        val quest = player.getQuestManager().getQuestById(questId);
        if(quest == null){
            return QuestState.QUEST_STATE_NONE;
        }

        return quest.getState();
    }

    @Override
    public int ShowReminder(GroupEventLuaContext context, int reminderId) {
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketDungeonShowReminderNotify(reminderId));
        return 0;
    }

    @Override
    public int RemoveEntityByConfigId(GroupEventLuaContext context, int groupId, EntityType entityType, int configId) {
        logger.debug("[LUA] Call RemoveEntityByConfigId");

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val scene = context.getSceneScriptManager().getScene();
        val entity = scene.getEntityByConfigId(configId, actualGroupId);

        if(entity == null || !entity.getEntityType().name().toUpperCase().equals(entityType.name())){
            return 1;
        }

        context.getSceneScriptManager().getScene().removeEntity(entity);

        return 0;
    }

    @Override
    public int CreateGroupTimerEvent(GroupEventLuaContext context, int groupId, String source, double time) {
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        return context.getSceneScriptManager().createGroupTimerEvent(actualGroupId, source, time);
    }

    @Override
    public int CancelGroupTimerEvent(GroupEventLuaContext context, int groupId, String source) {
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        return context.getSceneScriptManager().cancelGroupTimerEvent(actualGroupId, source);
    }

    @Override
    public int GetGroupSuite(GroupEventLuaContext context, int groupId) {
        //logger.warn("[LUA] Call GetGroupSuite with {}", groupID);
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        var instance = context.getSceneScriptManager().getGroupInstanceById(actualGroupId);
        if(instance != null) return instance.getActiveSuiteId();
        return 0;
    }

    @Override
    public int SetGroupReplaceable(GroupEventLuaContext context, int groupId, boolean value) {
        logger.warn("[LUA] Call SetGroupReplaceable with {} {}", groupId, value);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        var group = context.getSceneScriptManager().getCachedGroupInstanceById(actualGroupId);
        if(group != null && group.isReplaceable() != null) {
            group.setReplaceable(value);
            return 0;
        }
        return 1;
    }

    @Override
    public int[] GetSceneUidList(GroupEventLuaContext context) {
        logger.warn("[LUA] Call unchecked GetSceneUidList");
        //TODO check
        val scriptManager = context.getSceneScriptManager();
        if(scriptManager == null){
            return new int[0];
        }
        return scriptManager.getScene().getPlayers().stream().mapToInt(Player::getUid).toArray();
    }

    @Override
    public int GetSeaLampActivityPhase(GroupEventLuaContext context) {
        return handleUnimplemented();
    }

    @Override
    public int GadgetPlayUidOp(GroupEventLuaContext context, int groupId, int gadget_crucible, int var3, int var4, String var5, int var6) {
        return handleUnimplemented(groupId, gadget_crucible, var3, var4, var5, var6);
    }

    @Override
    public long GetServerTime(GroupEventLuaContext context) {
        logger.debug("[LUA] Call GetServerTime");
        //TODO check
        return new Date().getTime();
    }

    @Override
    public long GetServerTimeByWeek(GroupEventLuaContext context) {
        logger.debug("[LUA] Call GetServerTimeByWeek");
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    @Override
    public int GetCurTriggerCount(GroupEventLuaContext context) {
        logger.debug("[LUA] Call GetCurTriggerCount");
        //TODO check
        return context.getSceneScriptManager().getTriggerCount();
    }

    @Override
    public int GetChannellerSlabLoopDungeonLimitTime(GroupEventLuaContext context) {
        return handleUnimplemented();
    }

    @Override
    public boolean IsPlayerAllAvatarDie(GroupEventLuaContext context, int sceneUid) {
        logger.warn("[LUA] Call unimplemented IsPlayerAllAvatarDie {}", sceneUid);
        var playerEntities = context.getSceneScriptManager().getScene().getEntities().values().stream()
            .filter(e -> e.getEntityType().name().toUpperCase().equals(EntityType.AVATAR.name()))
            .toList();

        for (GameEntity p : playerEntities){
            var player = (EntityAvatar)p;
            if(player.isAlive()){
                return false;
            }
        }
        //TODO check
        return true;
    }

    @Override
    public int sendShowCommonTipsToClient(GroupEventLuaContext context, String title, String content, int closeTime) {
        logger.debug("[LUA] Call sendShowCommonTipsToClient with {}, {}, {}", title, content, closeTime);
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketShowCommonTipsNotify(title, content, closeTime));
        return 0;
    }

    @Override
    public int sendCloseCommonTipsToClient(GroupEventLuaContext context) {
        logger.debug("[LUA] Call unimplemented sendCloseCommonTipsToClient");
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketCloseCommonTipsNotify());
        return 0;
    }

    @Override
    public int CreateFatherChallenge(GroupEventLuaContext context, int challengeIndex, int challengeId, int timeLimit, LuaTable conditionTable) {
        logger.debug("[LUA] Call CreateFatherChallenge with {} {} {} {}",
            challengeIndex, challengeId, timeLimit, conditionTable);

        WorldChallenge challenge = ChallengeFactory.getChallenge(
            new ChallengeInfo(challengeIndex, challengeId, challengeIndex),
            List.of(conditionTable.getInt("success"), conditionTable.getInt("fail"), timeLimit),
            new ChallengeScoreInfo(conditionTable.getInt("success"), conditionTable.getInt("fail")),
            context.getSceneScriptManager().getScene(),
            context.getCurrentGroup()
        );

        if (challenge == null) return 1;

        context.getSceneScriptManager().getScene().setChallenge(challenge);
        return 0;
    }

    @Override
    public int StartFatherChallenge(GroupEventLuaContext context, int challengeIndex) {
        logger.debug("[LUA] Call StartFatherChallenge with {}", challengeIndex);
        WorldChallenge challenge = context.getSceneScriptManager().getScene().getChallenge();
        if (challenge == null || challenge.getInfo().getChallengeIndex() != challengeIndex) return 1;

        challenge.start();
        return 0;
    }

    @Override
    public int ModifyFatherChallengeProperty(GroupEventLuaContext context, int challengeId, FatherChallengeProperty propertyType, int value) {
        return handleUnimplemented(challengeId, propertyType.name(), value);
    }

    @Override
    public int SetChallengeEventMark(GroupEventLuaContext context, int challengeId, ChallengeEventMarkType eventMarkType) {
        return handleUnimplemented(challengeId, eventMarkType.name());
    }

    @Override
    public int AttachChildChallenge(GroupEventLuaContext context, int fatherChallengeIndex, int childChallengeIndex, int childChallengeId, LuaTable conditionArray, LuaTable var5, LuaTable conditionTable) {
        logger.debug("[LUA] Call AttachChildChallenge with {} {} {} {} {} {}",
            fatherChallengeIndex, childChallengeIndex, childChallengeId,
            printTable(conditionArray), printTable(var5), printTable(conditionTable));

        val challenge = ChallengeFactory.getChallenge(
            new ChallengeInfo(childChallengeIndex, childChallengeId, fatherChallengeIndex),
            Arrays.stream(conditionArray.getAsIntArray()).boxed().toList(),
            new ChallengeScoreInfo(conditionTable.getInt("success"), conditionTable.getInt("fail")),
            context.getSceneScriptManager().getScene(),
            context.getCurrentGroup()
        );

        val sceneChallenge = context.getSceneScriptManager().getScene().getChallenge();
        if (sceneChallenge == null || challenge == null
            || sceneChallenge.getInfo().getChallengeIndex() != fatherChallengeIndex) return 1;

        sceneChallenge.attachChild(challenge);
        return 0;
    }

    @Override
    public int CreateEffigyChallengeMonster(GroupEventLuaContext context, int var1, LuaTable var2Table) {
        return handleUnimplemented(var1, printTable(var2Table));
    }

    @Override
    public int GetEffigyChallengeMonsterLevel(GroupEventLuaContext context) {
        return handleUnimplemented();
    }

    @Override
    public int AddTeamEntityGlobalFloatValue(GroupEventLuaContext context, LuaTable sceneUidListTable, String var2, int var3) {
        return handleUnimplemented(printTable(sceneUidListTable), var2, var3);
    }

    @Override
    public int CreateBlossomChestByGroupId(GroupEventLuaContext context, int groupId, int chestConfigId) {
        logger.debug("[LUA] Call check CreateBlossomChestByGroupId with {} {}", groupId, chestConfigId);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val currentGroup = getGroupOrCurrent(context, groupId);
        if (currentGroup == null) return 1;

        val gadget = currentGroup.getGadgets().get(chestConfigId);
        val chestGadget = context.getSceneScriptManager().createGadget(actualGroupId, currentGroup.getGroupInfo().getBlockId(), gadget);
        if (chestGadget == null) return 1;

        val blossomManager = context.getSceneScriptManager().getScene().getWorld().getHost().getBlossomManager();
        val blossomSchedule = blossomManager.getBlossomSchedule().get(actualGroupId);
        if (blossomSchedule == null) return 1;

        blossomManager.getSpawnedChest().put(chestGadget.getConfigId(), actualGroupId);
        context.getSceneScriptManager().addEntity(chestGadget);
        context.getSceneScriptManager().getScene().broadcastPacket(
            new PacketBlossomChestCreateNotify(blossomSchedule.getRefreshId(), blossomSchedule.getCircleCampId()));
        return 0;
    }

    @Override
    public int GetBlossomScheduleStateByGroupId(GroupEventLuaContext context, int groupId) {
        logger.debug("[LUA] Call check GetBlossomScheduleStateByGroupId with {}", groupId);
        if (context.getCurrentGroup() == null) return -1;

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val blossomManager = context.getSceneScriptManager().getScene().getWorld().getHost().getBlossomManager();
        return Optional.ofNullable(blossomManager.getBlossomSchedule().get(actualGroupId))
            .map(BlossomSchedule::getState).orElse(-1);
    }

    @Override
    public int SetBlossomScheduleStateByGroupId(GroupEventLuaContext context, int groupId, int state) {
        logger.debug("[LUA] Call check SetBlossomScheduleStateByGroupId with {} {}", groupId, state);

        val blossomManager = context.getSceneScriptManager().getScene().getWorld().getHost().getBlossomManager();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val result = blossomManager.setBlossomState(actualGroupId, state);
        if (result && state == 1) { // there should only be one gadget of this blossom at this point, which is the operator
            context.getSceneScriptManager().getScene().getEntities().values().stream()
                .filter(entity -> entity.getGroupId() == actualGroupId).filter(EntityGadget.class::isInstance)
                .map(EntityGadget.class::cast).findFirst().ifPresent(gadget -> gadget.updateState(ScriptGadgetState.GearAction2));
        }
        return result ? 0 : 1;
    }

    @Override
    public int RefreshBlossomGroup(GroupEventLuaContext context, LuaTable configTable) {
        logger.debug("[LUA] Call check RefreshBlossomGroup with {}", printTable(configTable));

        val actualGroupId = getGroupIdOrCurrentId(context, configTable.optInt("group_id", 0));
        val group = getGroupOrCurrent(context, configTable.optInt("group_id", 0));
        if (group == null) return 1;

        val groupInstance = context.getSceneScriptManager().getGroupInstanceById(actualGroupId);
        int suiteIndex = configTable.getInt("suite");
        val suite = group.getSuiteByIndex(suiteIndex);
        if (suite == null || groupInstance == null) return 1;

        context.getSceneScriptManager().refreshGroup(groupInstance, suiteIndex, configTable.getBoolean("exclude_prev"));
        val blossomManager = context.getSceneScriptManager().getScene().getWorld().getHost().getBlossomManager();
        val schedule = blossomManager.getBlossomSchedule().get(actualGroupId);
        if (schedule == null) return 0;

        val spawnedChest = blossomManager.getSpawnedChest().values().stream()
            .filter(gid -> gid == schedule.getGroupId()).findFirst().orElse(null);
        context.getSceneScriptManager().callEvent(new ScriptArgs(
            actualGroupId, spawnedChest == null ? EventType.EVENT_GROUP_REFRESH : EventType.EVENT_BLOSSOM_PROGRESS_FINISH));
        return 0;
    }

    @Override
    public int RefreshBlossomDropRewardByGroupId(GroupEventLuaContext context, int groupId) {
        return handleUnimplemented(groupId);
    }

    @Override
    public int AddBlossomScheduleProgressByGroupId(GroupEventLuaContext context, int groupId) {
        logger.debug("[LUA] Call check AddBlossomScheduleProgressByGroupId with {}", groupId);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val blossomManager = context.getSceneScriptManager().getScene().getWorld().getHost().getBlossomManager();
        return blossomManager.addBlossomProgress(actualGroupId) ? 0 : 1;
    }

    @Override
    public int GetBlossomRefreshTypeByGroupId(GroupEventLuaContext context, int groupId) {
        logger.debug("[LUA] Call check GetBlossomRefreshTypeByGroupId with {}", groupId);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val blossomManager = context.getSceneScriptManager().getScene().getWorld().getHost().getBlossomManager();
        return Optional.ofNullable(blossomManager.getBlossomSchedule().get(actualGroupId))
            .map(BlossomSchedule::getRefreshType).map(BlossomRefreshType::getValue).orElse(2);
    }

    @Override
    public int RefreshHuntingClueGroup(GroupEventLuaContext context) {
        return handleUnimplemented();
    }

    @Override
    public int GetHuntingMonsterExtraSuiteIndexVec(GroupEventLuaContext context) {
        return handleUnimplemented();
    }

    @Override
    public int SetGroupTempValue(GroupEventLuaContext context, String name, int value, LuaTable var3Table) {
        return handleUnimplemented(name, value, printTable(var3Table));
    }

    @Override
    public int GetGroupTempValue(GroupEventLuaContext context, String name, LuaTable var2) {
        return handleUnimplemented(name, printTable(var2));
    }

    @Override
    public int FinishExpeditionChallenge(GroupEventLuaContext context) {
        return handleUnimplemented();
    }

    @Override
    public int ExpeditionChallengeEnterRegion(GroupEventLuaContext context, boolean var1) {
        return handleUnimplemented();
    }

    @Override
    public int StartSealBattle(GroupEventLuaContext context, int gadgetId, SealBattleParams battleParams) {
        return handleUnimplemented(gadgetId, battleParams);
        //TODO implement var2 containt int radius, int battle_time, int monster_group_id, int default_kill_charge, int auto_charge, int auto_decline, int max_energy, SealBattleType battleType
        // for type KILL_MONSTER watch group monster_group_id and afterwards trigger EVENT_SEAL_BATTLE_END with the result in param2
    }

    @Override
    public int InitTimeAxis(GroupEventLuaContext context, String var1, LuaTable var2, boolean var3) {
        return handleUnimplemented(var1, printTable(var2), var3);
        //TODO implement var1 == name? var2 == delay? var3 == should loop?
    }

    @Override
    public int EndTimeAxis(GroupEventLuaContext context, String var1) {
        return handleUnimplemented(var1);
        //TODO implement var1 == name?
    }

    @Override
    public int SetTeamEntityGlobalFloatValue(GroupEventLuaContext context, LuaTable sceneUidListTable, String var2, int var3) {
        return handleUnimplemented(printTable(sceneUidListTable), var2, var3);
    }

    @Override
    public int SetTeamServerGlobalValue(GroupEventLuaContext context, int sceneUid, String var2, int var3) {
        return handleUnimplemented(sceneUid, var2, var3);
    }

    @Override
    public int AddTeamServerGlobalValue(GroupEventLuaContext context, int ownerId, String sgvName, int value) {
        return handleUnimplemented(ownerId, sgvName, value);
    }

    @Override
    public int GetTeamServerGlobalValue(GroupEventLuaContext context, int ownerId, String sgvName, int value) {
        return handleUnimplemented(ownerId, sgvName, value);
    }

    @Override
    public int GetLanternRiteValue(GroupEventLuaContext context) {
        return handleUnimplemented();
    }

    @Override
    public int CreateMonsterFaceAvatar(GroupEventLuaContext context, LuaTable table) {
        return handleUnimplemented(printTable(table));
        //TODO implement var1 contains int entity_id, int[] monsters (cfgIds), int[] ranges, int angle
    }

    @Override
    public int ChangeToTargetLevelTag(GroupEventLuaContext context, int levelTag) {
        context.getSceneScriptManager().getScene().getPlayers().forEach(player -> player.setLevelTag(levelTag));
        return 0;
    }

    @Override
    public int AddSceneTag(GroupEventLuaContext context, int sceneId, int sceneTagId) {
        return handleUnimplemented(sceneId, sceneTagId);
    }

    @Override
    public int DelSceneTag(GroupEventLuaContext context, int sceneId, int sceneTagId) {
        return handleUnimplemented(sceneId, sceneTagId);
    }

    @Override
    public boolean CheckSceneTag(GroupEventLuaContext context, int sceneId, int sceneTagId) {
        logger.warn("[LUA] Call unimplemented CheckSceneTag with {}, {}", sceneId, sceneTagId);
        //TODO implement
        return false;
    }

    @Override
    public int StartHomeGallery(GroupEventLuaContext context, int galleryId, int uid) {
        return handleUnimplemented(galleryId, uid);
    }

    @Override
    public int StartGallery(GroupEventLuaContext context, int galleryId) {
        return handleUnimplemented(galleryId);
    }

    @Override
    public int StopGallery(GroupEventLuaContext context, int galleryId, boolean var2) {
        return handleUnimplemented(galleryId, var2);
    }

    @Override
    public int UpdatePlayerGalleryScore(GroupEventLuaContext context, int galleryId, LuaTable var2) {
        return handleUnimplemented(galleryId, printTable(var2));
        //TODO implement var2 contains int uid
    }

    @Override
    public int InitGalleryProgressScore(GroupEventLuaContext context, String name, int galleryId, LuaTable progress,
                                        GalleryProgressScoreUIType uiScoreType, GalleryProgressScoreType scoreType) {
        return handleUnimplemented(name, galleryId, printTable(progress), uiScoreType, scoreType);
    }

    @Override
    public int InitGalleryProgressWithScore(GroupEventLuaContext context, String name, int galleryId, LuaTable progress, int maxProgress,
                                            GalleryProgressScoreUIType uiScoreType, GalleryProgressScoreType scoreType) {
        return handleUnimplemented(name, galleryId, printTable(progress), maxProgress, uiScoreType, scoreType);
    }

    @Override
    public int AddGalleryProgressScore(GroupEventLuaContext context, String name, int galleryId, int score) {
        return handleUnimplemented(name, galleryId, score);
    }

    @Override
    public int GetGalleryProgressScore(GroupEventLuaContext context, String name, int galleryId) {
        return handleUnimplemented(name, galleryId);
    }

    @Override
    public int SetHandballGalleryBallPosAndRot(GroupEventLuaContext context, int galleryId, LuaTable positionTable, LuaTable rotationTable) {
        return handleUnimplemented(galleryId, printTable(positionTable), printTable(rotationTable));
    }

    @Override
    public int SendServerMessageByLuaKey(GroupEventLuaContext context, String messageKey, int[] targets) {
        return handleUnimplemented(messageKey, targets);
    }

    @Override
    public int TryReallocateEntityAuthority(GroupEventLuaContext context, int uid, int endConfig, int var3) {
        return handleUnimplemented(uid, endConfig, var3);
    }

    @Override
    public int ForceRefreshAuthorityByConfigId(GroupEventLuaContext context, int var1, int uid) {
        return handleUnimplemented(var1, uid);
    }

    @Override
    public int AddPlayerGroupVisionType(GroupEventLuaContext context, int[] uids, int[] visionTypeList) {
        return handleUnimplemented(uids, visionTypeList);
    }

    @Override
    public int DelPlayerGroupVisionType(GroupEventLuaContext context, int[] uids, int[] visionTypeList) {
        return handleUnimplemented(uids, visionTypeList);
    }

    @Override
    public int SetPlayerGroupVisionType(GroupEventLuaContext context, int[] uids, int[] visionTypeList) {
        return handleUnimplemented(uids, visionTypeList);
    }

    @Override
    public int MoveAvatarByPointArray(GroupEventLuaContext context, int uid, int targetId, LuaTable var3, String var4) {
        return handleUnimplemented(uid, targetId, printTable(var3), var4);
        //TODO implement var3 contains int speed, var4 is a json string
    }

    @Override
    public int MovePlayerToPos(GroupEventLuaContext context, int[] targetUIds, Vector pos, Vector rot, int radius, boolean isSkipUi) {
        logger.warn("[LUA] Call unchecked MovePlayerToPos with {}, {}, {}, {}, {}", targetUIds, pos, rot, radius, isSkipUi);
        //TODO implement var1 contains int[] uid_list, Position pos, int radius, Position rot
        return TransPlayerToPos(context, targetUIds, pos, rot, radius, isSkipUi); // todo this is probably not a full scene reload
    }

    @Override
    public int TransPlayerToPos(GroupEventLuaContext context, int[] targetUIds, Vector pos, Vector rot, int radius, boolean isSkipUi) {
        logger.warn("[LUA] Call unchecked TransPlayerToPos with {}, {}, {}, {}, {}", targetUIds, pos, rot, radius, isSkipUi);

        var scriptManager = context.getSceneScriptManager();
        if(scriptManager==null){
            return 2;
        }

        var scene = scriptManager.getScene();
        scene.getPlayers().stream().filter(p -> ArrayUtils.contains(targetUIds,p.getUid())).forEach(p -> {
            scene.removePlayer(p);
            scene.addPlayer(p);
            val playerPos = p.getPosition().set(pos);

            // Teleport packet
            p.sendPacket(new PacketPlayerEnterSceneNotify(p, EnterType.ENTER_GOTO, Lua, scene.getId(), playerPos));
        });
        return 0;
    }

    @Override
    public int PlayCutScene(GroupEventLuaContext context, int cutsceneId, int var2) {
        logger.warn("[LUA] Call untested PlayCutScene with {} {}", cutsceneId, var2);
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketCutsceneBeginNotify(cutsceneId));
        //TODO implement
        return 0;
    }

    @Override
    public int PlayCutSceneWithParam(GroupEventLuaContext context, int cutsceneId, int var2, LuaTable var3) {
        return handleUnimplemented(cutsceneId, var2, printTable(var3));
    }

    @Override
    public int ScenePlaySound(GroupEventLuaContext context, LuaTable soundInfo) {
        logger.debug("[LUA] Call unimplemented ScenePlaySound with {}", printTable(soundInfo));
        val soundName = soundInfo.optString("sound_name", null);
        val playType = soundInfo.optInt("play_type", 0);
        val isBroadcast = soundInfo.optBoolean("is_broadcast", true);
        val luaPlayPosition = soundInfo.getTable("play_pos");

        val playPosition = luaToPos(luaPlayPosition);
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketScenePlayerSoundNotify(new emu.grasscutter.utils.Position(playPosition), soundName, playType));
        return 0;
    }

    @Override
    public int BeginCameraSceneLook(GroupEventLuaContext context, LuaTable sceneLookParams) {
        logger.debug("[LUA] Call BeginCameraSceneLook with {}", printTable(sceneLookParams));
        val luaLookPos = sceneLookParams.getTable("look_pos");

        val cameraParams = new PacketBeginCameraSceneLookNotify.CameraSceneLookNotify();
        cameraParams.setLookPos(new emu.grasscutter.utils.Position(luaToPos(luaLookPos)));
        if(sceneLookParams.has("duration")) {
            cameraParams.setDuration(sceneLookParams.getFloat("duration"));
        }
        if(sceneLookParams.has("is_force")) {
            cameraParams.setForce(sceneLookParams.getBoolean("is_force"));
        }
        if(sceneLookParams.has("is_allow_input")) {
            cameraParams.setAllowInput(sceneLookParams.getBoolean("is_allow_input"));
        }
        if (sceneLookParams.has("is_set_follow_pos")) {
            cameraParams.setSetFollowPos(sceneLookParams.getBoolean("is_set_follow_pos"));
        }
        if (sceneLookParams.has("is_force_walk")) {
            cameraParams.setForceWalk(sceneLookParams.getBoolean("is_force_walk"));
        }
        if (sceneLookParams.has("is_change_play_mode")) {
            cameraParams.setChangePlayMode(sceneLookParams.getBoolean("is_change_play_mode"));
        }
        if(sceneLookParams.has("is_recover_keep_current")) {
            cameraParams.setRecoverKeepCurrent(sceneLookParams.getBoolean("is_recover_keep_current"));
        }
        if (sceneLookParams.has("is_set_screen_xy")) {
            val isSetScreenXY = sceneLookParams.getBoolean("is_set_screen_xy");
            cameraParams.setScreenXY(isSetScreenXY);
            if(isSetScreenXY && sceneLookParams.has("screen_x")) {
                cameraParams.setScreenX(sceneLookParams.getFloat("screen_x"));
            }
            if(isSetScreenXY && sceneLookParams.has("screen_y")) {
                cameraParams.setScreenX(sceneLookParams.getFloat("screen_y"));
            }
        }
        if (sceneLookParams.has("is_set_follow_pos")) {
            val isSetFollowPos = sceneLookParams.getBoolean("is_set_follow_pos");
            cameraParams.setSetFollowPos(isSetFollowPos);
            if(isSetFollowPos && sceneLookParams.has("follow_pos")) {
                val luaFollowPos = sceneLookParams.getTable("follow_pos");
                cameraParams.setFollowPos(ScriptUtils.luaToPos(luaFollowPos));
            }
        }
        if(sceneLookParams.has("is_broadcast")) {
            // TODO cameraParams.setBroadcast(sceneLookParams.getBoolean("is_broadcast"));
        }

        context.getSceneScriptManager().getScene().broadcastPacket(new PacketBeginCameraSceneLookNotify(cameraParams));
        return 0;
    }

    @Override
    public int SetPlayerEyePointStream(GroupEventLuaContext context, int var1, int var2, boolean var3) {
        return handleUnimplemented(var1, var2, var3);
    }

    @Override
    public int ClearPlayerEyePoint(GroupEventLuaContext context, int var1) {
        return handleUnimplemented();
    }

    @Override
    public int ShowReminderRadius(GroupEventLuaContext context, int var1, LuaTable var2, int var3) {
        return handleUnimplemented(var1, printTable(var2), var3);
    }

    @Override
    public int ShowClientGuide(GroupEventLuaContext context, String guideName) {
        logger.debug("[LUA] Call unimplemented ShowClientGuide with {}", guideName);
        if (GameData.getGuideTriggerDataStringMap().get(guideName) != null) {
            // if should handle by open state, dont send packet here
            // not entirely sure what return value is about
            // probably not needing this check statement here since the value comes from
            // the lua script
            return 1;
        }
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketShowClientGuideNotify(guideName));
        return 0;
    }

    @Override
    public int ActivateDungeonCheckPoint(GroupEventLuaContext context, int pointId) {
        logger.debug("[LUA] Call ActivateDungeonCheckPoint with {}", pointId);
        var dungeonManager = context.getSceneScriptManager().getScene().getDungeonManager();
        if(dungeonManager == null){
            return 1;
        }
        return dungeonManager.activateRespawnPoint(pointId) ? 0:2;
    }

    @Override
    public int SetWeatherAreaState(GroupEventLuaContext context, int var1, int var2) {
        logger.warn("[LUA] Call unimplemented SetWeatherAreaState with {} {}", var1, var2);
        if(var2 != 0) {
            return context.getSceneScriptManager().getScene().addWeatherArea(var1) ? 0 : 1;
        } else {
            return context.getSceneScriptManager().getScene().removeWeatherArea(var1) ? 0 : 1;
        }
    }

    @Override
    public int EnterWeatherArea(GroupEventLuaContext context, int weatherAreaId) {
        context.getSceneScriptManager().getScene().getPlayers().forEach(p -> {
            if(p.getWeatherAreaId() != weatherAreaId) p.updateWeather(p.getScene());
        });

        return 0;
    }

    @Override
    public boolean CheckIsInMpMode(GroupEventLuaContext context) {
        logger.debug("[LUA] Call CheckIsInMpMode");
        return context.getSceneScriptManager().getScene().getWorld().isMultiplayer();
    }


    /**
     * TODO properly implement
     * var3 might contain the next point, sometimes is a single int, sometimes multiple ints as array
     * var4 has RouteType route_type, bool turn_mode
     */
    @Override
    public int SetPlatformPointArray(GroupEventLuaContext context, int entityConfigId, int pointArrayId, LuaTable var3, LuaTable var4) {
        logger.warn("[LUA] Call half implemented SetPlatformPointArray with {} {} {} {}", entityConfigId, pointArrayId, printTable(var3), printTable(var4));

        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        val scene = context.getSceneScriptManager().getScene();
        val entity = scene.getEntityByConfigId(entityConfigId, groupId);
        if(entity == null){
            return 1;
        }
        if(!(entity instanceof EntityGadget entityGadget)){
            return 2; //todo maybe also check the gadget type?
        }

        var routeConfig = entityGadget.getRouteConfig();
        if(!(routeConfig instanceof PointArrayRoute)){
            routeConfig = new PointArrayRoute((entityGadget).getMetaGadget());
            entityGadget.setRouteConfig(routeConfig);
        }

        val configRoute = (PointArrayRoute) routeConfig;
        if(configRoute.getPointArrayId() == pointArrayId){
            return -1;
        }

        configRoute.setPointArrayId(pointArrayId);
        val pointIndexList = Arrays.stream(var3.getAsIntArray()).boxed().toList();
        if (!entityGadget.scheduleArrayPoints(pointArrayId, pointIndexList)) {
            return -1;
        }
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketPlatformChangeRouteNotify(entityGadget));
        return 0;
    }

    @Override
    public int SetPlatformRouteId(GroupEventLuaContext context, int entityConfigId, int routeId) {
        logger.info("[LUA] Call SetPlatformRouteId {} {}", entityConfigId, routeId);

        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        val scene = context.getSceneScriptManager().getScene();
        val entity = scene.getEntityByConfigId(entityConfigId, groupId);
        if(entity == null){
            return 1;
        }
        if(!(entity instanceof EntityGadget entityGadget)){
            return 2; //todo maybe also check the gadget type?
        }

        var routeConfig = entityGadget.getRouteConfig();
        if(!(routeConfig instanceof ConfigRoute)){
            routeConfig = new ConfigRoute((entityGadget).getMetaGadget());
            entityGadget.setRouteConfig(routeConfig);
        }

        val configRoute = (ConfigRoute) routeConfig;
        if(configRoute.getRouteId() == routeId){
            return 0;
        }

        configRoute.setRouteId(routeId);
        configRoute.setStartSceneTime(scene.getSceneTime());
        configRoute.setStartIndex(0);
        entityGadget.schedulePlatform();
        scene.broadcastPacket(new PacketPlatformChangeRouteNotify(entityGadget));
        return 0;
    }

    @Override
    public int StartPlatform(GroupEventLuaContext context, int configId) {
        logger.info("[LUA] Call StartPlatform {} ", configId);

        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        val scene = context.getSceneScriptManager().getScene();
        val entity = scene.getEntityByConfigId(configId, groupId);
        if(!(entity instanceof EntityGadget entityGadget)) {
            return 1;
        }

        return entityGadget.startPlatform() ? 0 : 2;
    }

    @Override
    public int StopPlatform(GroupEventLuaContext context, int configId) {
        logger.info("[LUA] Call StopPlatform {} ", configId);

        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        val scene = context.getSceneScriptManager().getScene();
        val entity = scene.getEntityByConfigId(configId, groupId);
        if(!(entity instanceof EntityGadget entityGadget)) {
            return 1;
        }

        return entityGadget.stopPlatform() ? 0 : 2;
    }

    @Override
    public int CreateChannellerSlabCampRewardGadget(GroupEventLuaContext context, int configId) {
        logger.warn("[LUA] Call unimplemented CreateChannellerSlabCampRewardGadget {}", configId);
        var group = context.getCurrentGroup();
        if(group == null){
            return 1;
        }
        createGadget(context.getSceneScriptManager(), configId, group);
        //TODO implement fully
        return 0;
    }

    @Override
    public int AssignPlayerShowTemplateReminder(GroupEventLuaContext context, int var1, LuaTable var2) {
        return handleUnimplemented(var1, printTable(var2));
        //TODO implement var2 contains LuaTable param_uid_vec, LuaTable param_vec int[] uid_vec
    }

    @Override
    public int RevokePlayerShowTemplateReminder(GroupEventLuaContext context, int var1, LuaTable var2) {
        return handleUnimplemented(var1, printTable(var2));
    }

    @Override
    public int UnlockForce(GroupEventLuaContext context, int force) {
        logger.debug("[LUA] Call UnlockForce {}", force);
        context.getSceneScriptManager().getScene().unlockForce(force);
        return 0;
    }

    @Override
    public int LockForce(GroupEventLuaContext context, int force) {
        logger.debug("[LUA] Call LockForce {}", force);
        context.getSceneScriptManager().getScene().lockForce(force);
        return 0;
    }

    private static int killGroupEntityWithTable(SceneScriptManager sceneScriptManager, SceneGroup group, LuaTable lists){
        // get targets
        val monsterList = lists.getTable("monsters");
        val gadgetList = lists.getTable("gadgets");
        val monsterSize = monsterList != null ? monsterList.getSize() : 0;
        val gadgetSize = gadgetList != null ? gadgetList.getSize() : 0;

        int[] targets = new int[monsterSize + gadgetSize];
        int targetsIndex = 0;
        if(monsterList != null) {
            for (int i = 1; i <= monsterSize; i++, targetsIndex++) {
                targets[targetsIndex] = monsterList.optInt(i, -1);
            }
        }
        if(gadgetList != null) {
            for (int i = 1; i <= gadgetSize; i++, targetsIndex++) {
                targets[targetsIndex] = gadgetList.optInt(i, -1);
            }
        }

        // kill targets if exists
        for(int cfgId : targets){
            var entity = sceneScriptManager.getScene().getEntityByConfigId(cfgId, group.getGroupInfo().getId());
            if (entity == null || cfgId == 0) {
                continue;
            }
            sceneScriptManager.getScene().killEntity(entity, 0);
        }
        return 0;
    }

    @Override
    public int KillGroupEntityByCfgIds(GroupEventLuaContext context, int groupId, int[] monsters, int[] gadgets) {
        val sceneManager = context.getSceneScriptManager();

        val group = getGroupOrCurrent(context, groupId);
        if (group == null) {
            return 10;
        }
        int[] targets = new int[monsters.length + gadgets.length];
        int targetsIndex = 0;
        for (int i = 0; i < monsters.length; i++, targetsIndex++) {
            targets[targetsIndex] = monsters[i];
        }
        for (int i = 0; i < gadgets.length; i++, targetsIndex++) {
            targets[targetsIndex] = gadgets[i];
        }

        // kill targets if exists
        for(int cfgId : targets){
            var entity = sceneManager.getScene().getEntityByConfigId(cfgId, group.getGroupInfo().getId());
            if (entity == null || cfgId == 0) {
                continue;
            }
            sceneManager.getScene().killEntity(entity, 0);
        }
        return 0;
    }

    @Override
    public int KillGroupEntityByPolicy(GroupEventLuaContext context, int groupId, GroupKillPolicy policy) {
        val sceneManager = context.getSceneScriptManager();

        val group = getGroupOrCurrent(context, groupId);
        if (group == null) {
            return 10;
        }

        var targets = new ArrayList<SceneObject>();
        if(policy==GROUP_KILL_MONSTER || policy == GROUP_KILL_ALL){
            val monsters = group.getMonsters();
            if(monsters != null)
                targets.addAll(monsters.values());
        }
        if(policy == GROUP_KILL_GADGET || policy == GROUP_KILL_ALL) {
            val gadgets = group.getGadgets();
            if(gadgets != null)
                targets.addAll(gadgets.values());
        }

        // kill targets if exists
        targets.forEach(o -> {
            var entity = sceneManager.getScene().getEntityByConfigId(o.getConfigId(), group.getGroupInfo().getId());
            if (entity == null) {
                return;
            }
            sceneManager.getScene().killEntity(entity, 0);
        });
        return 0;
    }

    @Override
    public int GetMonsterIdByEntityId(GroupEventLuaContext context, int entityId) {
        var entity = context.getSceneScriptManager().getScene().getEntityById(entityId);
        if(!(entity instanceof EntityMonster)){
            return 0;
        }
        return ((EntityMonster) entity).getMonsterData().getId();
    }

    @Override
    public int GetMonsterConfigId(GroupEventLuaContext context, int entityId) {
        var entity = context.getSceneScriptManager().getScene().getEntityById(entityId);
        if(!(entity instanceof EntityMonster)){
            return 0;
        }
        return entity.getConfigId();
    }

    @Override
    public int GetMonsterID(GroupEventLuaContext context, int var1) {
        //TODO implement var1 type
        return handleUnimplemented(var1);
    }

    @Override
    public int GetEntityIdByConfigId(GroupEventLuaContext context, int configId) {
        logger.warn("[LUA] Call GetEntityIdByConfigId with {}", configId);
        //TODO check
        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        val scene = context.getSceneScriptManager().getScene();
        var entity = scene.getEntityByConfigId(configId, groupId);
        return entity != null ? entity.getId() : 0;
    }

    @Override
    public int GetAvatarEntityIdByUid(GroupEventLuaContext context, int uid) {
        logger.warn("[LUA] Call unchecked GetAvatarEntityIdByUid with {}", uid);
        //TODO check
        var entity = context.getSceneScriptManager().getScene().getEntities().values().stream()
            .filter(e -> e instanceof EntityAvatar && ((EntityAvatar)e).getPlayer().getUid() == uid)
            .findFirst();
        return entity.map(GameEntity::getId).orElse(0);
    }

    @Override
    public Vector GetPosByEntityId(GroupEventLuaContext context, int entityId) {
        logger.warn("[LUA] Call unchecked GetPosByEntityId with {}", entityId);
        //TODO check
        var entity = context.getSceneScriptManager().getScene().getEntityById(entityId);
        return entity != null? entity.getPosition() : null;
    }

    @Override
    public Vector GetRotationByEntityId(GroupEventLuaContext context, int entityId) {
        logger.debug("[LUA] Call unchecked GetRotationByEntityId with {}", entityId);
        //TODO check
        var entity = context.getSceneScriptManager().getScene().getEntityById(entityId);
        return entity != null? entity.getRotation() : null;
    }

    @Override
    public ActivityOpenAndCloseTime GetActivityOpenAndCloseTimeByScheduleId(GroupEventLuaContext context, int scheduleId) {logger.debug("[LUA] Call GetActivityOpenAndCloseTimeByScheduleId with {}", scheduleId);

        val activityConfig = ActivityManager.getScheduleActivityConfigMap().get(scheduleId);

        if(activityConfig == null)
            return null;

        val result = new ActivityOpenAndCloseTime();
        result.setOpenTime(activityConfig.getBeginTime());
        result.setCloseTime(activityConfig.getEndTime());
        return result;
    }

    @Override
    public int GetGameHour(GroupEventLuaContext context) {
        return context.getSceneScriptManager().getScene().getWorld().getGameTimeHours();
    }

    @Override
    public int ActivateGroupLinkBundle(GroupEventLuaContext context, int groupId) {
        return handleUnimplemented(groupId);
    }

    @Override
    public int ActivateGroupLinkBundleByBundleId(GroupEventLuaContext context, int bundleId) {
        return handleUnimplemented(bundleId);
    }


    /**
     * TODO implement
     * @param context
     * @param givingId The id if the giving element found in [GivingData]
     * @param groupId The groupdId of the group containing the gadget
     * @param gadgetCfgId The gadgets target configId
     * @return 0 if success, something else if failed
     */
    @Override
    public int ActiveGadgetItemGiving(GroupEventLuaContext context, int givingId, int groupId, int gadgetCfgId) {
        return handleUnimplemented(givingId, groupId, gadgetCfgId);
    }

    @Override
    public int AddChessBuildingPoints(GroupEventLuaContext context, int groupId, int param2, int uid, int pointsToAdd) {
        return handleUnimplemented(groupId, param2, uid, pointsToAdd);
    }

    @Override
    public int AddEntityGlobalFloatValueByConfigId(GroupEventLuaContext context, LuaTable param1Table, String param2, int param3) {
        return handleUnimplemented(printTable(param1Table), param2, param3);
    }

    /**
     * TODO implement
     * @param context
     * @param uid
     * @param param2  probably the name of the data field
     * @param param3
     * @return
     */
    @Override
    public int AddExhibitionAccumulableData(GroupEventLuaContext context, int uid, String param2, int param3) {
        return handleUnimplemented(uid, param2, param3);
    }


    /**
     * TODO implement
     * @param context
     * @param uid
     * @param param2 probably the name of the data field
     * @param param3
     * @param exhibitionPlayType
     * @param galleryId
     * @return
     */
    @Override
    public int AddExhibitionAccumulableDataAfterSuccess(GroupEventLuaContext context, int uid, String param2, int param3, ExhibitionPlayType exhibitionPlayType, int galleryId) {
        return handleUnimplemented(uid, param2, param3, exhibitionPlayType.name(), galleryId);
    }


    /**
     * TODO implement
     * @param context
     * @param uid
     * @param param2  probably the name of the data field
     * @param param3
     * @return
     */
    @Override
    public int AddExhibitionReplaceableData(GroupEventLuaContext context, int uid, String param2, int param3) {
        return handleUnimplemented(uid, param2, param3);
    }

    /**
     * TODO implement
     * @param context
     * @param uid
     * @param param2 probably the name of the data field
     * @param param3
     * @param param4 contains the fields "play_type" is part of the enum [ExhibitionPlayType] and "gallery_id"
     * @return
     */
    @Override
    public int AddExhibitionReplaceableDataAfterSuccess(GroupEventLuaContext context, int uid, String param2, int param3, LuaTable param4) {
        return handleUnimplemented(uid, param2, param3, printTable(param4));
    }

    @Override
    public int AddFleurFairMultistagePlayBuffEnergy(GroupEventLuaContext context, int groupId, int param2, int uid, int bonusId) {
        return handleUnimplemented(groupId, param2, uid, bonusId);
    }

    @Override
    public int AddGadgetPlayProgress(GroupEventLuaContext context, int param1, int param2, int progressChange) {
        return handleUnimplemented(param1, param2, progressChange);
    }

    @Override
    public int AddIrodoriChessBuildingPoints(GroupEventLuaContext context, int groupId, int param2, int points) {
        return handleUnimplemented(groupId, param2, points);
    }

    @Override
    public int AddIrodoriChessTowerServerGlobalValue(GroupEventLuaContext context, int groupId, int param2, int param3, int delta) {
        return handleUnimplemented(groupId, param2, param3, delta);
    }

    @Override
    public int AddMechanicusBuildingPoints(GroupEventLuaContext context, int groupId, int param2, int uid, int delta) {
        return handleUnimplemented(groupId, param2, uid, delta);
    }

    @Override
    public int AddRegionRecycleProgress(GroupEventLuaContext context, int regionId, int delta) {
        return handleUnimplemented(regionId, delta);
    }

    @Override
    public int AddRegionSearchProgress(GroupEventLuaContext context, int regionId, int delta) {
        return handleUnimplemented(regionId, delta);
    }

    @Override
    public int AddRegionalPlayVarValue(GroupEventLuaContext context, int uid, int regionId, int delta) {
        return handleUnimplemented(uid, regionId, delta);
    }

    @Override
    public int AddSceneMultiStagePlayUidValue(GroupEventLuaContext context, int groupId, int param2, String param3, int uid, int param5) {
        return handleUnimplemented(groupId, param2, param3, uid, param5);
    }

    @Override
    public int AddScenePlayBattleProgress(GroupEventLuaContext context, int groupId, int progress) {
        return handleUnimplemented(groupId, progress);
    }

    @Override
    public int ExecuteActiveGroupLua(org.anime_game_servers.gi_lua.script_lib.GroupEventLuaContext groupEventLuaContext, int groupId, String functionName, LuaTable callParamsTable) {
        return handleUnimplemented(groupId, functionName, printTable(callParamsTable));
    }

    @Override
    public int ExecuteGroupLua(org.anime_game_servers.gi_lua.script_lib.GroupEventLuaContext groupEventLuaContext, int groupId, String functionName, LuaTable callParamsTable) {
        return handleUnimplemented(groupId, functionName, printTable(callParamsTable));
    }

    /**
     * TODO implement
     * @param context
     * @param param1Table contains the following fields: param_index:int, param_list:Table, param_uid_list:Table,
     *                    duration:int, target_uid_list:Table
     * @return
     */
    @Override
    public int AssignPlayerUidOpNotify(GroupEventLuaContext context, LuaTable param1Table) {
        return handleUnimplemented(printTable(param1Table));
    }

    @Override
    public int CreateTreasureMapSpotRewardGadget(GroupEventLuaContext context, int gadgetCfgId) {
        return handleUnimplemented(gadgetCfgId);
    }

    /**
     * TODO implement
     */
    @Override
    public int updateBundleMarkShowStateByGroupId(GroupEventLuaContext groupEventLuaContext, int groupId, boolean val2) {
        return handleUnimplemented(groupId, val2);
    }



}
