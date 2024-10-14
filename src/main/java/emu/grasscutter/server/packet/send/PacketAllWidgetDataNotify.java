package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.AllWidgetDataNotify;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.lunch_box.LunchBoxData;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.manage_slot.WidgetSlotData;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.manage_slot.WidgetSlotTag;

import java.util.List;

public class PacketAllWidgetDataNotify extends BaseTypedPacket<AllWidgetDataNotify> {
    public PacketAllWidgetDataNotify(Player player) {
        super(new AllWidgetDataNotify());
        // TODO: Implement this

            // If you want to implement this, feel free to do so. :)
        proto.setLunchBoxData(new LunchBoxData());
            // Maybe it's a little difficult, or it makes you upset :(
        proto.setOneOfGatherPointDetectorDataList(List.of());
            // So, goodbye, and hopefully sometime in the future o(*￣▽￣*)ブ
        proto.setCoolDownGroupDataList(List.of());
            // I'll see your PR with a title that says (・∀・(・∀・(・∀・*)
        proto.setAnchorPointList(List.of());
            // "Complete implementation of widget functionality" b（￣▽￣）d　
        proto.setClientCollectorDataList(List.of());
            // Good luck, my boy.
        proto.setNormalCoolDownDataList(List.of());

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

