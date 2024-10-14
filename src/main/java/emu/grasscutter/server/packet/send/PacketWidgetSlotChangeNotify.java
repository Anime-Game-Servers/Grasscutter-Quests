package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.manage_slot.WidgetSlotChangeNotify;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.manage_slot.WidgetSlotData;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.manage_slot.WidgetSlotOp;

public class PacketWidgetSlotChangeNotify extends BaseTypedPacket<WidgetSlotChangeNotify> {
    public PacketWidgetSlotChangeNotify(WidgetSlotOp op) {
        super(new WidgetSlotChangeNotify());
        proto.setOp(op);
        val widgetSlotData = new WidgetSlotData();
        widgetSlotData.setActive(true);
        proto.setSlot(widgetSlotData);
    }

    public PacketWidgetSlotChangeNotify(int materialId) {
        super(new WidgetSlotChangeNotify());
        val widgetSlotData = new WidgetSlotData();
        widgetSlotData.setActive(true);
        widgetSlotData.setMaterialId(materialId);
        proto.setSlot(widgetSlotData);
    }
}
