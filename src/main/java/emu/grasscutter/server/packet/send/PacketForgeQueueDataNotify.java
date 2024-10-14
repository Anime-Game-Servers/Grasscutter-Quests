package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.forge.ForgeQueueData;
import org.anime_game_servers.multi_proto.gi.messages.item.forge.ForgeQueueDataNotify;

import java.util.List;
import java.util.Map;

public class PacketForgeQueueDataNotify extends BaseTypedPacket<ForgeQueueDataNotify> {
	public PacketForgeQueueDataNotify(Map<Integer, ForgeQueueData> queueData, List<Integer> removedQueues) {
        super(new ForgeQueueDataNotify());
        proto.setRemovedForgeQueueList(removedQueues);
        proto.setForgeQueueMap(queueData);
	}
}
