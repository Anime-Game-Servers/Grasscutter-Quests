package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.manage_slot.GetWidgetSlotRsp;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.manage_slot.WidgetSlotData;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.manage_slot.WidgetSlotTag;

import java.util.List;

public class PacketGetWidgetSlotRsp extends BaseTypedPacket<GetWidgetSlotRsp> {
    public PacketGetWidgetSlotRsp(Player player) {
        super(new GetWidgetSlotRsp());
        if (player.getWidgetId() == 0) {  // TODO: check this logic later, it was null-checking an int before which made it dead code
            proto.setSlotList(List.of());
        } else {
            val widgetSlotDataFirst = new WidgetSlotData();
            widgetSlotDataFirst.setActive(true);
            widgetSlotDataFirst.setMaterialId(player.getWidgetId());

            val widgetSlotDataSecond = new WidgetSlotData();
            widgetSlotDataSecond.setTag(WidgetSlotTag.WIDGET_SLOT_ATTACH_AVATAR);

            proto.setSlotList(List.of(widgetSlotDataFirst, widgetSlotDataSecond));
        }
    }
}
