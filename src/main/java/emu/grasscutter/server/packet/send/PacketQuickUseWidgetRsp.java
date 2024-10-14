package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.use.QuickUseWidgetRsp;

public class PacketQuickUseWidgetRsp extends BaseTypedPacket<QuickUseWidgetRsp> {
    public PacketQuickUseWidgetRsp(int materialId, Retcode retcode) {
        super(new QuickUseWidgetRsp());
        proto.setMaterialId(materialId);
        proto.setRetcode(retcode);
    }
}
