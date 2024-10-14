package emu.grasscutter.game.entity.gadget;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.CodexViewpointData;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.player.Player;
import org.anime_game_servers.multi_proto.gi.messages.gadget.GadgetInteractReq;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.SceneGadgetInfo;


public class GadgetViewPoint extends  GadgetContent{


    public GadgetViewPoint(EntityGadget gadget) {
        super(gadget);
    }

    @Override
    public boolean onInteract(Player player, GadgetInteractReq req) {
        int groupId = this.getGadget().getGroupId();
        int configId = this.getGadget().getConfigId();
        CodexViewpointData viewPoint = GameData.getViewCodexByGroupdCfg(groupId, configId);

        if(viewPoint != null){
            player.getCodex().checkUnlockedViewPoints(viewPoint);
            return true;
        }

        return false;
    }

    @Override
    public void onBuildProto(SceneGadgetInfo gadgetInfo) {
        // Shouldn't require explicit mapping to set content
    }

}
