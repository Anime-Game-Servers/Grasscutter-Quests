package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.combine.CombineDataNotify;

import java.util.Set;

public class PacketCombineDataNotify extends BaseTypedPacket<CombineDataNotify> {
    public PacketCombineDataNotify(Set<Integer> unlockedCombines) {
        super(new CombineDataNotify());
        proto.setCombineIdList(unlockedCombines.stream().toList());
	}
}
