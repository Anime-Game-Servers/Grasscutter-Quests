package emu.grasscutter.game.entity.gadget;

import emu.grasscutter.game.entity.EntityBaseGadget;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.player.Player;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.gadget.GadgetInteractReq;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.AbilityGadgetInfo;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.SceneGadgetInfo;

public class GadgetAbility extends GadgetContent {
    private EntityBaseGadget parent;

    public GadgetAbility(EntityGadget gadget, EntityBaseGadget parent) {
        super(gadget);
        this.parent = parent;
    }

    public boolean onInteract(Player player, GadgetInteractReq req) {
        return false;
    }

    @Override
    public void onBuildProto(SceneGadgetInfo gadgetInfo) {
        if (this.parent == null) {
            return;
        }

        val abilityGadgetInfo = new AbilityGadgetInfo(parent.getCampId(), parent.getCampType(), parent.getId());
        gadgetInfo.setContent(new SceneGadgetInfo.Content.AbilityGadget(abilityGadgetInfo));
    }

}
