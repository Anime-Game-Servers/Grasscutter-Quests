package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import org.anime_game_servers.multi_proto.gi.messages.item.management.DestroyMaterialRsp;

import java.util.ArrayList;
import java.util.List;

public class PacketDestroyMaterialRsp extends BaseTypedPacket<DestroyMaterialRsp> {
    public PacketDestroyMaterialRsp(Int2IntMap returnMaterialMap) {
        super(new DestroyMaterialRsp());
        List<Integer> itemIdList = new ArrayList<>();
        List<Integer> itemCountList = new ArrayList<>();
        returnMaterialMap.forEach((id, count) -> {
            itemIdList.add(id);
            itemCountList.add(count);
        });
        proto.setItemIdList(itemIdList);
        proto.setItemCountList(itemCountList);
    }
}
