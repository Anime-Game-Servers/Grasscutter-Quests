package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.CompoundDataNotify;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.CompoundQueueData;

import java.util.List;
import java.util.Set;

public class PacketCompoundDataNotify extends BaseTypedPacket<CompoundDataNotify> {
    public PacketCompoundDataNotify(Set<Integer> unlockedCompounds, List<CompoundQueueData> compoundQueueData) {
        super(new CompoundDataNotify());
        proto.setUnlockCompoundList(unlockedCompounds.stream().toList());
        proto.setCompoundQueueDataList(compoundQueueData);
    }
}
