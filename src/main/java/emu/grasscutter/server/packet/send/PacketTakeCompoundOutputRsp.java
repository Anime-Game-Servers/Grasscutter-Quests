package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.TakeCompoundOutputRsp;

import java.util.List;

public class PacketTakeCompoundOutputRsp extends BaseTypedPacket<TakeCompoundOutputRsp> {
    public PacketTakeCompoundOutputRsp(List<GameItem> itemList, Retcode retcode) {
        super(new TakeCompoundOutputRsp());
        proto.setItemList(itemList.stream().map(GameItem::toItemParam).toList());
        proto.setRetcode(retcode);
    }
}
