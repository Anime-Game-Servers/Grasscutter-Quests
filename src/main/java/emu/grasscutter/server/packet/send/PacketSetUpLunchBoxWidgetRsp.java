package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.lunch_box.LunchBoxData;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.lunch_box.SetUpLunchBoxWidgetRsp;

public class PacketSetUpLunchBoxWidgetRsp extends BaseTypedPacket<SetUpLunchBoxWidgetRsp> {
    public PacketSetUpLunchBoxWidgetRsp(LunchBoxData lunchBoxData) {
        super(new SetUpLunchBoxWidgetRsp());
        proto.setLunchBoxData(lunchBoxData);
    }
}
