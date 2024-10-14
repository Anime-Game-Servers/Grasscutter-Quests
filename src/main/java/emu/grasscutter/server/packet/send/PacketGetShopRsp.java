package emu.grasscutter.server.packet.send;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.shop.ShopInfo;
import emu.grasscutter.game.shop.ShopSystem;
import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.utils.Utils;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.item.ItemParam;
import org.anime_game_servers.multi_proto.gi.messages.shop.GetShopRsp;
import org.anime_game_servers.multi_proto.gi.messages.shop.Shop;
import org.anime_game_servers.multi_proto.gi.messages.shop.ShopGoods;

import java.util.ArrayList;

public class PacketGetShopRsp extends BaseTypedPacket<GetShopRsp> {
    public PacketGetShopRsp(Player inv, int shopType) {
        super(new GetShopRsp());

        // TODO: CityReputationLevel
        val shop = new Shop(shopType);
        shop.setCityId(1); //mock
        shop.setCityReputationLevel(10); //mock

        ShopSystem manager = Grasscutter.getGameServer().getShopSystem();
        if (manager.getShopData().get(shopType) != null) {
            val list = manager.getShopData().get(shopType);
            val goodsList = new ArrayList<ShopGoods>();
            for (ShopInfo info : list) {
                val goods = new ShopGoods();
                goods.setGoodsId(info.getGoodsId());
                goods.setGoodsItem(new ItemParam(info.getGoodsItem().getId(), info.getGoodsItem().getCount()));
                goods.setScoin(info.getScoin());
                goods.setHcoin(info.getHcoin());
                goods.setBuyLimit(info.getBuyLimit());
                goods.setBeginTime(info.getBeginTime());
                goods.setEndTime(info.getEndTime());
                goods.setMinLevel(info.getMinLevel());
                goods.setMaxLevel(info.getMaxLevel());
                goods.setMcoin(info.getMcoin());
                goods.setDisableType(info.getDisableType());
                goods.setSecondarySheetId(info.getSecondarySheetId());
                if (info.getCostItemList() != null) {
                    goods.setCostItemList(
                        info.getCostItemList().stream()
                            .map(x -> new ItemParam(x.getId(), x.getCount()))
                            .toList());
                }
                if (info.getPreGoodsIdList() != null) {
                    goods.setPreGoodsIdList(info.getPreGoodsIdList());
                }

                int currentTs = Utils.getCurrentSeconds();
                val currentShopLimit = inv.getGoodsLimit(info.getGoodsId());
                val nextRefreshTime = ShopSystem.getShopNextRefreshTime(info);
                if (currentShopLimit != null) {
                    if (currentShopLimit.getNextRefreshTime() < currentTs) { // second game day
                        currentShopLimit.setHasBoughtInPeriod(0);
                        currentShopLimit.setNextRefreshTime(nextRefreshTime);
                    }
                    goods.setBoughtNum(currentShopLimit.getHasBoughtInPeriod());
                    goods.setNextRefreshTime(currentShopLimit.getNextRefreshTime());
                } else {
                    inv.addShopLimit(goods.getGoodsId(), 0, nextRefreshTime); // save generated refresh time
                    goods.setNextRefreshTime(nextRefreshTime);
                }

                goodsList.add(goods);
            }
            shop.setGoodsList(goodsList);
        }

        inv.save();
        proto.setShop(shop);
    }
}
