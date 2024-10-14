package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.CompoundQueueData;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.GetCompoundDataRsp;

import java.util.List;
import java.util.Set;

public class PacketGetCompoundDataRsp extends BaseTypedPacket<GetCompoundDataRsp> {
    public PacketGetCompoundDataRsp(Set<Integer> unlockedCompounds, List<CompoundQueueData> compoundQueueData) {
        super(new GetCompoundDataRsp());
        proto.setUnlockCompoundList(unlockedCompounds.stream().toList());
        proto.setCompoundQueDataList(compoundQueueData);
        proto.setRetcode(Retcode.RET_SUCC);
    }
}
