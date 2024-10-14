package emu.grasscutter.game.entity.gadget;

import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.player.Player;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.gadget.GadgetInteractReq;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.SceneGadgetInfo;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.ScreenInfo;

public class GadgetScreen extends GadgetContent {
    public GadgetScreen(EntityGadget gadget) {
        super(gadget);
    }

    @Override
    public boolean onInteract(Player player, GadgetInteractReq req) {
        return false;
    }

    @Override
    public void onBuildProto(SceneGadgetInfo gadgetInfo) {
        val screen = new ScreenInfo(1);
        //screen.setProjectorEntityId(getGadget().getScene().getEntityByConfigId(178001, getGadget().getGroupId()).getId())

        gadgetInfo.setContent(new SceneGadgetInfo.Content.ScreenInfo(screen));
    }
}
