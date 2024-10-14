package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.inventory.Inventory;
import emu.grasscutter.game.inventory.InventoryTab;
import emu.grasscutter.game.inventory.ItemType;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketQuickUseWidgetRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.use.QuickUseWidgetReq;

public class HandlerQuickUseWidgetReq extends TypedPacketHandler<QuickUseWidgetReq> {
    /*
    * WARNING: with the consuming of material widget ( Example: bomb ),
    * this is just a implement designed to the decreasing of count
    *
    * ### Known Bug: No effects after using item but decrease. ###
    *
    * If you know which Packet could make the effects, feel free to contribute!
    * */
    @Override
    public void handle(GameSession session, byte[] header, QuickUseWidgetReq req) throws Exception {
        Player pl = session.getPlayer();
        synchronized (pl) {
            int materialId = pl.getWidgetId();
            Inventory inventory = session.getPlayer().getInventory();
            InventoryTab inventoryTab = inventory.getInventoryTab(ItemType.ITEM_MATERIAL);
            GameItem item = inventoryTab.getItemById(materialId);
            int remain = item.getCount();
            if (remain > 0) {
                remain--;
                inventory.removeItem(item,1);// decrease count
                session.send(new PacketQuickUseWidgetRsp(materialId, remain <= 0 ? Retcode.RET_FAIL : Retcode.RET_SUCC));
                Grasscutter.getLogger().warn("class has no effects in the game, feel free to implement it");
                // but no effects in the game, feel free to implement it!
            }
        }
    }
}
