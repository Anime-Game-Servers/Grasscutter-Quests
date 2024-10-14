package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.manage_cooldown.WidgetCoolDownData;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.manage_cooldown.WidgetCoolDownNotify;

import java.util.List;

public class PacketWidgetCoolDownNotify extends BaseTypedPacket<WidgetCoolDownNotify> {
    public PacketWidgetCoolDownNotify(int id, long coolDownTime, boolean isSuccess) {
        super(new WidgetCoolDownNotify());
        val widgetCoolDownData = new WidgetCoolDownData();
        widgetCoolDownData.setId(id);
        widgetCoolDownData.setCoolDownTime(coolDownTime);
        widgetCoolDownData.setSuccess(isSuccess);
        proto.setGroupCoolDownDataList(List.of(widgetCoolDownData));
    }
}
