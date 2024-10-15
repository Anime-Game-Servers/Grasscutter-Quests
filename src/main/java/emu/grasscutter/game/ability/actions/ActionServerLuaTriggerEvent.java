package emu.grasscutter.game.ability.actions;

import com.github.davidmoten.guavamini.Lists;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.GameEntity;
import lombok.val;
import org.anime_game_servers.gi_lua.models.ScriptArgs;
import org.anime_game_servers.gi_lua.models.constants.EventType;

import java.util.Arrays;

@AbilityAction(AbilityModifierAction.Type.ServerLuaTriggerEvent)
public class ActionServerLuaTriggerEvent extends AbilityActionHandler {

    @Override
    public boolean execute(Ability ability, AbilityModifierAction action, byte[] abilityData, GameEntity target) {
        val scene = target.getScene();
        val scriptManager = scene.getScriptManager();

        val params = switch (action.luaCallType) {
            case "FromGroup" -> {
                var groupId = target.getGroupId();
                yield Lists.newArrayList(new ScriptArgs(groupId, EventType.EVENT_LUA_NOTIFY));
            }
            case "SpecificGroup" -> Arrays.stream(action.callParamList)
                .mapToObj(groupId -> new ScriptArgs(groupId, EventType.EVENT_LUA_NOTIFY))
                .toList();
            default -> {
                Grasscutter.getLogger().error("Unimplemented ActionServerLuaCall {}", action.luaCallType);
                yield null;
            }
        };

        if(params == null) return false;
        params.forEach(p -> {
            p.setTargetEntityId(target.getId());
            p.setSourceEntityId(target.getId());
            p.setEventSource(action.sourceName);

            p.setParam1( (int) action.param1.get(ability));
            p.setParam2( (int) action.param2.get(ability));
            p.setParam3( (int) action.param3.get(ability));

            scriptManager.callEvent(p);
        });
        return true;
    }
}
