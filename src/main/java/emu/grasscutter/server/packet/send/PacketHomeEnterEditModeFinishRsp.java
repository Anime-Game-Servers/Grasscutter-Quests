package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.home.HomeEnterEditModeFinishRsp;

public class PacketHomeEnterEditModeFinishRsp extends BaseTypedPacket<HomeEnterEditModeFinishRsp> {
    public PacketHomeEnterEditModeFinishRsp() {
        super(new HomeEnterEditModeFinishRsp());
    }
}
