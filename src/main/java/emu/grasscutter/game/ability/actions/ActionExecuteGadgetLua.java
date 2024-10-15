package emu.grasscutter.game.ability.actions;

import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.GameEntity;

@AbilityAction(AbilityModifierAction.Type.ExecuteGadgetLua)
public class ActionExecuteGadgetLua extends AbilityActionHandler {
    @Override
    public boolean execute(Ability ability, AbilityModifierAction action,byte[]  abilityData, GameEntity target) {
        GameEntity owner = ability.getOwner();

        if(owner.getEntityController() != null) {
            owner.getEntityController().onClientExecuteRequest(owner,
                (int) action.param1.get(ability),
                (int) action.param2.get(ability),
                (int) action.param3.get(ability)
            );
            return true;
        }

        if (target.getEntityController() != null) {
            target.getEntityController().onClientExecuteRequest(target,
                (int) action.param1.get(ability),
                (int) action.param2.get(ability),
                (int) action.param3.get(ability)
            );
            return true;
        }

        return false;
    }
}
