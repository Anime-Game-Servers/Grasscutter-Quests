package emu.grasscutter.game.entity.gadget;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.entity.gadget.chest.BossChestInteractHandler;
import emu.grasscutter.game.managers.blossom.BlossomManager;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.proto.BossChestInfoOuterClass.BossChestInfo;
import emu.grasscutter.net.proto.GadgetInteractReqOuterClass.GadgetInteractReq;
import emu.grasscutter.net.proto.InterOpTypeOuterClass.InterOpType;
import emu.grasscutter.net.proto.InteractTypeOuterClass;
import emu.grasscutter.net.proto.InteractTypeOuterClass.InteractType;
import emu.grasscutter.net.proto.ResinCostTypeOuterClass;
import emu.grasscutter.net.proto.SceneGadgetInfoOuterClass.SceneGadgetInfo;
import emu.grasscutter.scripts.constants.ScriptGadgetState;
import emu.grasscutter.server.packet.send.PacketGadgetInteractRsp;

import java.util.List;

public class GadgetChest extends GadgetContent {

    public GadgetChest(EntityGadget gadget) {
        super(gadget);
    }

    public boolean onInteract(Player player, GadgetInteractReq req) {
        var chestInteractHandlerMap = getGadget().getScene().getWorld().getServer().getWorldDataSystem().getChestInteractHandlerMap();
        var handler = chestInteractHandlerMap.get(getGadget().getGadgetData().getJsonName());
        if (handler == null) {
            Grasscutter.getLogger().warn("Could not found the handler of this type of Chests {}", getGadget().getGadgetData().getJsonName());
            return false;
        }

        if (req.getOpType() == InterOpType.INTER_OP_TYPE_START && handler.isTwoStep()) {
            player.sendPacket(new PacketGadgetInteractRsp(getGadget(), InteractType.INTERACT_TYPE_OPEN_CHEST, InterOpType.INTER_OP_TYPE_START));
            return false;
        }else {
            boolean success;
            if (handler instanceof BossChestInteractHandler bossChestInteractHandler) {
                success = bossChestInteractHandler.onInteract(this, player,
                    req.getResinCostType()== ResinCostTypeOuterClass.ResinCostType.RESIN_COST_TYPE_CONDENSE);
            }else {
                success = handler.onInteract(this, player);
            }
            if (!success) {
                return false;
            }

            getGadget().updateState(ScriptGadgetState.ChestOpened);
            player.sendPacket(new PacketGadgetInteractRsp(this.getGadget(), InteractTypeOuterClass.InteractType.INTERACT_TYPE_OPEN_CHEST));

            return true;
        }
    }

    public void onBuildProto(SceneGadgetInfo.Builder gadgetInfo) {
        if (getGadget().getMetaGadget() == null) {
            return;
        }

        var bossChest = getGadget().getMetaGadget().boss_chest;
        if (bossChest != null) {
            List<Integer> playersUid = getGadget().getScene().getPlayers().stream().map(Player::getUid).toList();
            gadgetInfo.setBossChest(BossChestInfo.newBuilder()
                    .setMonsterConfigId(bossChest.monster_config_id)
                    .setResin(bossChest.resin)
                    .addAllQualifyUidList(playersUid)
                    .addAllRemainUidList(playersUid)
                    .build());
        }

        BlossomManager blossomManager = getGadget().getScene().getWorld().getOwner().getBlossomManager();
        if (blossomManager != null) {
            gadgetInfo.setBlossomChest(blossomManager.getChestInfo(getGadget().getGadgetId()));
        }
    }
}
