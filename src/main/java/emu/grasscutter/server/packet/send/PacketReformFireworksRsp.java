package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.firework.ReformFireworksRsp;

public class PacketReformFireworksRsp extends BaseTypedPacket<ReformFireworksRsp> {
    public PacketReformFireworksRsp() {
        super(new ReformFireworksRsp());
    }
}
