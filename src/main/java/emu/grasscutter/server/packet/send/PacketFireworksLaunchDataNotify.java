package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.firework.FireworksLaunchDataNotify;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.firework.FireworksLaunchSchemeData;

import java.util.List;

public class PacketFireworksLaunchDataNotify extends BaseTypedPacket<FireworksLaunchDataNotify> {
    public PacketFireworksLaunchDataNotify(FireworksLaunchSchemeData notify) {
        super(new FireworksLaunchDataNotify());
        proto.setLastUseSchemeId(1);
        proto.setSchemeDataList(List.of(notify));
    }
}
