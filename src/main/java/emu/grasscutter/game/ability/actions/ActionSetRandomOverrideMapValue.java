package emu.grasscutter.game.ability.actions;

import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.GameEntity;
import org.anime_game_servers.multi_proto.gi.messages.ability.action.AbilityActionSetRandomOverrideMapValue;

@AbilityAction(AbilityModifierAction.Type.SetRandomOverrideMapValue)
public class ActionSetRandomOverrideMapValue extends AbilityActionHandler {
    @Override
    public boolean execute(Ability ability, AbilityModifierAction action, byte[] abilityData, GameEntity target) {
        AbilityActionSetRandomOverrideMapValue valueProto;
        try {
            valueProto = AbilityActionSetRandomOverrideMapValue.parseBy(abilityData, ability.getPlayerOwner().getSession().getVersion());
        } catch (Exception e) {
            return false;
        }

        float value = valueProto.getRandomValue();
        float valueRangeMin = action.valueRangeMin.get(ability);
        float valueRangeMax = action.valueRangeMax.get(ability);

        if(value < valueRangeMin || value > valueRangeMax) {
            logger.warn("Tried setting value out of range: {} inside [{}, {}]", value, valueRangeMin, valueRangeMax);
            return true;
        }

        ability.getAbilitySpecials().put(action.overrideMapKey, value);

        return true;
    }
}
