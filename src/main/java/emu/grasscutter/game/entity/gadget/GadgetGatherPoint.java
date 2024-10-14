package emu.grasscutter.game.entity.gadget;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.GatherData;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.ActionReason;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.gadget.GadgetInteractReq;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.GatherGadgetInfo;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.SceneGadgetInfo;

/**
 * Spawner for the gather objects
 */
public class GadgetGatherPoint extends GadgetContent {
    private final GatherData gatherData;
    private final EntityGadget gatherObjectChild;

    public GadgetGatherPoint(EntityGadget gadget) {
        super(gadget);
        this.gatherData = GameData.getGatherDataMap().get(gadget.getPointType());

        val scene = gadget.getScene();
        gatherObjectChild = new EntityGadget(scene, gatherData.getGadgetId(), gadget.getBornPos());

        gatherObjectChild.setBlockId(gadget.getBlockId());
        gatherObjectChild.setConfigId(gadget.getConfigId());
        gatherObjectChild.setGroupId(gadget.getGroupId());
        gatherObjectChild.getRotation().set(gadget.getRotation());
        gatherObjectChild.setState(gadget.getState());
        gatherObjectChild.setPointType(gadget.getPointType());
        gatherObjectChild.setMetaGadget(gadget.getMetaGadget());
        gatherObjectChild.buildContent();

        gadget.getChildren().add(gatherObjectChild);
        scene.addEntity(gatherObjectChild);
    }

    public int getItemId() {
        return this.gatherData.getItemId();
    }

    public boolean isForbidGuest() {
        return this.gatherData.isForbidGuest();
    }

    public boolean onInteract(Player player, GadgetInteractReq req) {
        GameItem item = new GameItem(getItemId(), 1);

        player.getInventory().addItem(item, ActionReason.Gather);

        return true;
    }

    @Override
    public void onBuildProto(SceneGadgetInfo gadgetInfo) {
        //todo does official use this for the spawners?
        val gatherGadgetInfo = new GatherGadgetInfo(this.getItemId(), this.isForbidGuest());
        gadgetInfo.setContent(new SceneGadgetInfo.Content.GatherGadget(gatherGadgetInfo));
    }
}
