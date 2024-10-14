package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.forge.ForgeDataNotify;
import org.anime_game_servers.multi_proto.gi.messages.item.forge.ForgeQueueData;

import java.util.Map;
import java.util.Set;

public class PacketForgeDataNotify extends BaseTypedPacket<ForgeDataNotify> {
    public PacketForgeDataNotify(Set<Integer> unlockedItem, int numQueues, Map<Integer, ForgeQueueData> queueData) {
        super(new ForgeDataNotify());
        proto.setForgeIdList(unlockedItem.stream().toList());
        proto.setMaxQueueNum(numQueues);
        proto.setForgeQueueMap(queueData);
	}
}
