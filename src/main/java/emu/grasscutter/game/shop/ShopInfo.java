package emu.grasscutter.game.shop;

import emu.grasscutter.data.common.ItemParamData;
import lombok.Getter;
import lombok.Setter;
import org.anime_game_servers.game_data_models.data.shop.ShopGoodsData;
import org.anime_game_servers.game_data_models.data.shop.ShopRefreshType;
import static org.anime_game_servers.game_data_models.data.general.UnsetTypesKt.UnsetInt;
import static org.anime_game_servers.game_data_models.data.general.UnsetTypesKt.UnsetLong;

import java.util.ArrayList;
import java.util.List;

public class ShopInfo {
    @Getter @Setter private int goodsId;
    @Getter @Setter private ItemParamData goodsItem;
    @Getter @Setter private int scoin;
    @Getter @Setter private List<ItemParamData> costItemList;
    @Getter @Setter private int boughtNum = 0;
    @Getter @Setter private int buyLimit;
    @Getter @Setter private int beginTime;
    @Getter @Setter private int endTime;
    @Getter @Setter private int minLevel;
    @Getter @Setter private int maxLevel;
    @Getter @Setter private List<Integer> preGoodsIdList = new ArrayList<>();
    @Getter @Setter private int mcoin;
    @Getter @Setter private int hcoin;
    @Getter @Setter private int disableType = 0;
    @Getter @Setter private int secondarySheetId;

    private String refreshType;

    @Setter private transient ShopRefreshType shopRefreshType;
    @Getter @Setter private int shopRefreshParam;

    private int getOrDefault(int value, int defaultValue){
        return value != UnsetInt ? value : defaultValue;
    }
    private int getOrDefault(int value){
        return getOrDefault(value, 0);
    }
    private long getOrDefault(long value, long defaultValue){
        return value != UnsetLong ? value : defaultValue;
    }
    private long getOrDefault(long value){
        return getOrDefault(value, 0);
    }

    public ShopInfo(ShopGoodsData sgd) {
        this.goodsId = sgd.getGoodsId();
        this.goodsItem = new ItemParamData(getOrDefault(sgd.getItemId()), sgd.getItemCount());
        this.scoin = getOrDefault(sgd.getCostScoin());
        this.mcoin = getOrDefault(sgd.getCostMcoin());
        this.hcoin = getOrDefault(sgd.getCostHcoin());
        this.buyLimit = getOrDefault(sgd.getBuyLimit());

        this.beginTime = (int) getOrDefault(sgd.getBeginTimestamp());
        this.endTime = (int) getOrDefault(sgd.getEndTimestamp(), 1924992000);

        this.minLevel = getOrDefault(sgd.getMinPlayerLevel());
        this.maxLevel = getOrDefault(sgd.getMaxPlayerLevel(), 61);
        this.costItemList = sgd.getCostItems().stream()
            .filter(x -> x.getItemId() > 0)
            .map(x -> new ItemParamData(x.getItemId(), x.getCount()))
            .toList();
        this.secondarySheetId = getOrDefault(sgd.getSecondarySheetId());
        this.shopRefreshType = sgd.getRefreshType();
        this.shopRefreshParam = getOrDefault(sgd.getRefreshParam());
    }

    public ShopRefreshType getShopRefreshType() {
        if (refreshType == null)
            return ShopRefreshType.SHOP_REFRESH_NONE;
        return switch (refreshType) {
            case "SHOP_REFRESH_DAILY" -> ShopRefreshType.SHOP_REFRESH_DAILY;
            case "SHOP_REFRESH_WEEKLY" -> ShopRefreshType.SHOP_REFRESH_WEEKLY;
            case "SHOP_REFRESH_MONTHLY" -> ShopRefreshType.SHOP_REFRESH_MONTHLY;
            default -> ShopRefreshType.SHOP_REFRESH_NONE;
        };
    }

    private boolean evaluateVirtualCost(ItemParamData item) {
        return switch (item.getId()) {
            case 201 -> {this.hcoin += item.getCount(); yield true;}
            case 203 -> {this.mcoin += item.getCount(); yield true;}
            default -> false;
        };
    }

    public void removeVirtualCosts() {
        if (this.costItemList != null)
            this.costItemList.removeIf(this::evaluateVirtualCost);
    }
}
