package emu.grasscutter.game.ability.actions;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction.DropType;
import emu.grasscutter.data.binout.config.ConfigLevelEntity;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.entity.EntityItem;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.props.SceneType;
import emu.grasscutter.utils.Position;
import org.anime_game_servers.multi_proto.gi.messages.ability.action.AbilityActionGenerateElemBall;

@AbilityAction(AbilityModifierAction.Type.GenerateElemBall)
public class ActionGenerateElemBall extends AbilityActionHandler {
    @Override
    public boolean execute(Ability ability, AbilityModifierAction action, byte[] abilityData, GameEntity target) {
        GameEntity owner = ability.getOwner();

        AbilityActionGenerateElemBall generateElemBall;
        try {
            generateElemBall = AbilityActionGenerateElemBall.parseBy(abilityData, ability.getPlayerOwner().getSession().getVersion());
        } catch (Exception e) {
            return false;
        }

        //Check if we should allow elem ball generation
        if(action.dropType == DropType.LevelControl) {
            String levelEntityConfig = owner.getScene().getSceneData().getLevelEntityConfig();
            ConfigLevelEntity config = GameData.getConfigLevelEntityDataMap().get(levelEntityConfig);
            if(config != null && config.getDropElemControlType() != null && config.getDropElemControlType().compareTo("None") == 0) {
                logger.warn("This level config don't allow element balls");
                return true;
            }
        } else if(action.dropType == DropType.BigWorldOnly) {
            if(owner.getScene().getSceneData().getSceneType() != SceneType.SCENE_WORLD) {
                logger.warn("This level config only allows element balls on big world");
                return true;
            }
        } //Else the drop is forced

        var energy = action.baseEnergy.get(ability) * action.ratio.get(ability);
        if(energy <= 0.0) return true;

        var itemData = GameData.getItemDataMap().get(action.configID);
        if(itemData == null) {
            logger.warn("configID {} not found", action.configID);
            return false;
        }

        if(itemData.getItemUse() == null || itemData.getItemUse().isEmpty()) {
            logger.warn("Item {} has no item use array", action.configID);
            return true;
        }

        var itemUse = itemData.getItemUse().get(0);
        double requiredEnergy;
        switch(itemUse.getUseOp()) {
            case ITEM_USE_ADD_ELEM_ENERGY: requiredEnergy = Integer.parseInt(itemUse.getUseParam()[1]); break;
            case ITEM_USE_ADD_ALL_ENERGY: requiredEnergy = Integer.parseInt(itemUse.getUseParam()[0]); break;
            default:
                logger.warn("UseOp not implemented", itemUse.getUseOp());
                return false;
        }

        var amountGenerated = (int)Math.ceil(energy / requiredEnergy);
        if(amountGenerated >= 21) {
            logger.warn("Attempt to generate more than 20 element balls {}", amountGenerated);
            return false;
        }

        logger.debug("Generating {} of {} element balls", amountGenerated, action.configID);
        for(int i = 0; i < amountGenerated; i++) {
            EntityItem energyBall = new EntityItem(owner.getScene(), (owner instanceof EntityAvatar avatar) ? avatar.getPlayer() : null, itemData, new Position(generateElemBall.getPos()), new Position(generateElemBall.getRot()), 1);
            owner.getScene().addEntity(energyBall);
        }

        return true;
    }
}

