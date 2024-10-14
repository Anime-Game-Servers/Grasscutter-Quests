package emu.grasscutter.game.entity.gadget;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.GadgetInteractData.InteractActionType;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.player.Player;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.gadget.GadgetInteractReq;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.SceneGadgetInfo;

public class GadgetObject extends GadgetContent{
    public GadgetObject(EntityGadget gadget) {
        super(gadget);
    }

    @Override
    public boolean onInteract(Player player, GadgetInteractReq req) {
        if (this.getGadget() == null) return false;
        if (this.getGadget().getMetaGadget() == null) return false;
        val interactID = this.getGadget().getMetaGadget().getInteractId();
        if (interactID == 0) return false;
        val interactDataEntry = GameData.getGadgetInteractDataMap().values().stream().filter(x -> x.getInteractId() == interactID).toList().get(0);

        //TODO: check conditions

        //do the thing
        for (val action : interactDataEntry.getActionList()) {
            if (action.getActionType() != null) {
                executeInteractAction(action.getActionType(), action.getParam());
            }
        }

        //remove items
        for (val costItem : interactDataEntry.getCostItems()) {
            val gameItem = player.getInventory().getItemById(costItem.getId());
            player.getInventory().removeItem(gameItem, costItem.getCount());
        }

        return false;
    }

    private void executeInteractAction(InteractActionType action, int[] params) {
        switch (action) {
            case INTERACT_ACTION_NONE -> {
            }
            case INTERACT_ACTION_STATE -> this.getGadget().updateState(params[0]);
            default -> Grasscutter.getLogger().warn("Encountered unimplemented gadget interact action! {}", action);
        }
    }

    @Override
    public void onBuildProto(SceneGadgetInfo gadgetInfo) {

    }
}
