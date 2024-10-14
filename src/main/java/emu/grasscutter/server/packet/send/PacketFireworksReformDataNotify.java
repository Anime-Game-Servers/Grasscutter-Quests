package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.firework.FireworksReformData;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.firework.FireworksReformDataNotify;

import java.util.List;

public class PacketFireworksReformDataNotify extends BaseTypedPacket<FireworksReformDataNotify> {
    public PacketFireworksReformDataNotify(FireworksReformData pinfo) {
        super(new FireworksReformDataNotify());
        proto.setFireworksReformDataList(List.of(pinfo));
    }
}
