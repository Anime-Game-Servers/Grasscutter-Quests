package emu.grasscutter.game.ability.actions;

import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.props.CampTargetType;
import emu.grasscutter.utils.Position;
import org.anime_game_servers.multi_proto.gi.messages.ability.action.AbilityActionCreateGadget;

@AbilityAction(AbilityModifierAction.Type.CreateGadget)
public class ActionCreateGadget extends AbilityActionHandler {

    @Override
    public boolean execute(Ability ability, AbilityModifierAction action,byte[] abilityData, GameEntity target) {
        if(!action.byServer) {
            logger.debug("Action not executed by server");

            return true;
        }

        var entity = ability.getOwner();
        AbilityActionCreateGadget createGadget;
        try {
            createGadget = AbilityActionCreateGadget.parseBy(abilityData, ability.getPlayerOwner().getSession().getVersion());
        } catch (Exception e) {
            return false;
        }

        var entityCreated = new EntityGadget(entity.getScene(), action.gadgetID, new Position(createGadget.getPos()), new Position(createGadget.getRot()), action.campID, CampTargetType.getTypeByName(action.campTargetType).getValue());
        if(action.ownerIsTarget)
            entityCreated.setOwner(target);
        else
        entityCreated.setOwner(entity);

        entity.getScene().addEntity(entityCreated);

        logger.info("Gadget {} created at pos {} rot {}", action.gadgetID, entityCreated.getPosition(), entityCreated.getRotation());

        return true;
    }

}
