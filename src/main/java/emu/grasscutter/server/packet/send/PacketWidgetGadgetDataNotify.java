package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.manage_gadget.WidgetGadgetData;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.manage_gadget.WidgetGadgetDataNotify;

import java.util.List;

public class PacketWidgetGadgetDataNotify extends BaseTypedPacket<WidgetGadgetDataNotify> {
    public PacketWidgetGadgetDataNotify(int gadgetId, List<Integer> gadgetEntityIdList) {
        super(new WidgetGadgetDataNotify());
        val widgetGadgetData = new WidgetGadgetData();
        widgetGadgetData.setGadgetId(gadgetId);
        widgetGadgetData.setGadgetEntityIdList(gadgetEntityIdList);
        proto.setWidgetGadgetData(widgetGadgetData);
    }

    public PacketWidgetGadgetDataNotify(int gadgetId, int gadgetEntityIdList) {
        this(gadgetId, List.of(gadgetEntityIdList));
    }
}
