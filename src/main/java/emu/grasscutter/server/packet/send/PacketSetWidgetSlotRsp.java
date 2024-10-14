package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.manage_slot.SetWidgetSlotRsp;

public class PacketSetWidgetSlotRsp extends BaseTypedPacket<SetWidgetSlotRsp> {
    public PacketSetWidgetSlotRsp(int materialId) {
        super(new SetWidgetSlotRsp());
        proto.setMaterialId(materialId);
    }
}
