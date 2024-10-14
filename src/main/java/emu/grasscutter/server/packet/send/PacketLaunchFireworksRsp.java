package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.firework.LaunchFireworksRsp;

public class PacketLaunchFireworksRsp extends BaseTypedPacket<LaunchFireworksRsp> {
    public PacketLaunchFireworksRsp() {
        super(new LaunchFireworksRsp());
    }
}
