package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.activity.ActivityConfigItem;
import emu.grasscutter.game.activity.ActivityManager;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.shop.ActivityShopSheetInfo;
import org.anime_game_servers.multi_proto.gi.messages.shop.GetActivityShopSheetInfoRsp;

import java.util.ArrayList;
import java.util.List;

public class PacketGetActivityShopSheetInfoRsp extends BaseTypedPacket<GetActivityShopSheetInfoRsp> {


    public PacketGetActivityShopSheetInfoRsp(int shopType) {
        super(new GetActivityShopSheetInfoRsp());

        var sheetInfo = GameData.getActivityShopDataMap().get(shopType);
        ActivityConfigItem activityConfigItem = null;

        if (sheetInfo != null) {
            activityConfigItem = ActivityManager.getScheduleActivityConfigMap().get(sheetInfo.getScheduleId());
        }

        proto.setShopType(shopType);

        if (sheetInfo == null || activityConfigItem == null) {
            proto.setRetcode(Retcode.RET_SHOP_NOT_OPEN);
            return;
        }

        List<ActivityShopSheetInfo> sheetInfos = new ArrayList<>(sheetInfo.getSheetList().size());
        for (int id : sheetInfo.getSheetList()) {
            sheetInfos.add(new ActivityShopSheetInfo(
                id,
                (int)activityConfigItem.getBeginTime().getTime(),
                (int)activityConfigItem.getEndTime().getTime()));
        }
        proto.setSheetInfoList(sheetInfos);
    }
}
