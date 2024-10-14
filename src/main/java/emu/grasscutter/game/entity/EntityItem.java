package emu.grasscutter.game.entity;

import emu.grasscutter.data.excels.ItemData;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.ActionReason;
import emu.grasscutter.game.props.EntityIdType;
import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.server.packet.send.PacketGadgetInteractRsp;
import emu.grasscutter.utils.Position;
import emu.grasscutter.utils.ProtoHelper;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.gadget.GadgetInteractReq;
import org.anime_game_servers.multi_proto.gi.messages.gadget.InteractType;
import org.anime_game_servers.multi_proto.gi.messages.general.Vector;
import org.anime_game_servers.multi_proto.gi.messages.general.ability.AbilitySyncStateInfo;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.*;

import java.util.List;

public class EntityItem extends EntityBaseGadget {
    @Getter private final GameItem item;
    @Getter private final long guid;
    @Getter private final boolean share;

    public EntityItem(Scene scene, Player player, ItemData itemData, Position pos, int count) {
        this(scene, player, itemData, pos, count, true);
    }

    public EntityItem(Scene scene, Player player, ItemData itemData, Position pos, Position rotation, int count) {
        this(scene, player, itemData, pos, rotation, count, true);
    }

    public EntityItem(Scene scene, Player player, ItemData itemData, Position pos, int count, boolean share) {
        this(scene, player, itemData, pos, null, count, share);
    }

    // In official game, some drop items are shared to all players, and some other items are independent to all players
    // For example, if you killed a monster in MP mode, all players could get drops but rarity and number of them are different
    // but if you broke regional mine, when someone picked up the drop then it disappeared
    public EntityItem(Scene scene, Player player, ItemData itemData, Position pos, Position rotation, int count, boolean share) {
        super(scene, pos, rotation);
        this.id = getScene().getWorld().getNextEntityId(EntityIdType.GADGET);
        this.guid = player == null ? scene.getWorld().getHost().getNextGameGuid() : player.getNextGameGuid();
        this.item = new GameItem(itemData, count);
        this.share = share;
    }

    public ItemData getItemData() {
        return this.getItem().getItemData();
    }

    public int getCount() {
        return this.getItem().getCount();
    }

    @Override
    public int getGadgetId() {
        return this.getItemData().getGadgetId();
    }

    @Override public Int2FloatMap getFightProperties() {return null;}

    @Override
    public void onInteract(Player player, GadgetInteractReq interactReq) {
        // check drop owner to avoid someone picked up item in others' world
        if (!this.isShare()) {
            int dropOwner = (int) (this.getGuid() >> 32);
            if (dropOwner != player.getUid()) {
                return;
            }
        }

        this.getScene().removeEntity(this);
        GameItem item = new GameItem(this.getItemData(), this.getCount());

        // Add to inventory
        boolean success = player.getInventory().addItem(item, ActionReason.SubfieldDrop);
        if (success) {
            if (!this.isShare()) { // not shared drop
                player.sendPacket(new PacketGadgetInteractRsp(this, InteractType.INTERACT_PICK_ITEM));
            } else {
                this.getScene().broadcastPacket(new PacketGadgetInteractRsp(this, InteractType.INTERACT_PICK_ITEM));
            }
        }
    }

    @Override
    public SceneEntityInfo toProto() {
        val protoBornPos = new Vector();
        val protoPos = getPosition().toProto();
        val protoRot = getRotation().toProto();
        val aiInfo = new SceneEntityAiInfo(true, protoBornPos);
        val authority = new EntityAuthorityInfo(new AbilitySyncStateInfo(), new EntityRendererChangedInfo(), aiInfo, protoBornPos);

        val entityInfo = new SceneEntityInfo(ProtEntityType.PROT_ENTITY_GADGET, getId());
        entityInfo.setMotionInfo(new MotionInfo(protoPos, protoRot, new Vector()));
        entityInfo.setAnimatorParaList(List.of(new AnimatorParameterValueInfoPair()));
        entityInfo.setEntityClientData(new EntityClientData());
        entityInfo.setEntityAuthorityInfo(authority);
        entityInfo.setLifeState(1);

        PropPair pair = new PropPair(PlayerProperty.PROP_LEVEL.getId(), ProtoHelper.newPropValue(PlayerProperty.PROP_LEVEL, 1));
        entityInfo.setPropList(List.of(pair));

        val gadgetInfo = new SceneGadgetInfo(this.getItemData().getGadgetId());
        gadgetInfo.setContent(new SceneGadgetInfo.Content.TrifleItem(this.getItem().toProto()));
        gadgetInfo.setBornType(GadgetBornType.GADGET_BORN_IN_AIR);
        gadgetInfo.setAuthorityPeerId(this.getWorld().getHostPeerId());
        gadgetInfo.setEnableInteract(true);

        entityInfo.setEntity(new SceneEntityInfo.Entity.Gadget(gadgetInfo));

        return entityInfo;
    }

}
