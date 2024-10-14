package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.shop.BuyGoodsRsp;
import org.anime_game_servers.multi_proto.gi.messages.shop.ShopGoods;

public class PacketBuyGoodsRsp extends BaseTypedPacket<BuyGoodsRsp> {
    public PacketBuyGoodsRsp(int shopType, int boughtNum, ShopGoods sg) {
        super(new BuyGoodsRsp());

        proto.setShopType(shopType);
        proto.setBuyCount(boughtNum);
        sg.setBoughtNum(boughtNum);
        proto.setGoods(sg);
    }
}
