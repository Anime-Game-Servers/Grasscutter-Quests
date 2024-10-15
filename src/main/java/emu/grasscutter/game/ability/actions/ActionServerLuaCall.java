package emu.grasscutter.game.ability.actions;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import lombok.val;
import org.anime_game_servers.gi_lua.models.ScriptArgs;

@AbilityAction(AbilityModifierAction.Type.ServerLuaCall)
public class ActionServerLuaCall extends AbilityActionHandler {
    @Override
    public boolean execute(Ability ability, AbilityModifierAction action, byte[] abilityData, GameEntity target) {
        switch (action.luaCallType) {
            case "FromGroup" -> {
                return serverLuaCallForGroup(target.getGroupId(), ability, action, abilityData, target);
            }
            case "SpecificGroup" -> {
                var returnBool = true;
                for (val groupId : action.callParamList) {
                    returnBool = returnBool && serverLuaCallForGroup(groupId, ability, action, abilityData, target);
                }
                return returnBool;
            }
            default -> {
                Grasscutter.getLogger().error("Unimplemented ActionServerLuaCall {}", action.luaCallType);
//                "AbilityGroupSourceGroup"
//                "CurChallengeGroup"
//                "CurGalleryControlGroup"
//                "CurRogueBossGroup"
//                "CurScenePlay"
            }
        }
        return false;
    }

    private boolean serverLuaCallForGroup(int groupId, Ability ability, AbilityModifierAction action, byte[] abilityData, GameEntity target) {
        val scriptManager = target.getScene().getScriptManager();
        val scriptArgs = new ScriptArgs(groupId, 0)
            .setTargetEntityId(target.getId())
            .setSourceEntityId(target.getId());
        val paramsCount = action.paramNum;
        val scriptParams = new Integer[paramsCount];
        switch (paramsCount) {
            case 3:
                scriptParams[2] = (int) action.param3.get(ability);
            case 2:
                scriptParams[1] = (int) action.param2.get(ability);
            case 1:
                scriptParams[0] = (int) action.param1.get(ability);
        }
        return scriptManager.callGroupLuaFunction(action.funcName, scriptArgs, (Object[]) scriptParams);
    }
}
