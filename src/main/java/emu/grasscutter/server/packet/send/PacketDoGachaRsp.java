package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.common.ItemParamData;
import emu.grasscutter.game.gacha.GachaBanner;
import emu.grasscutter.game.gacha.PlayerGachaBannerInfo;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.wishing.DoGachaRsp;
import org.anime_game_servers.multi_proto.gi.messages.wishing.GachaItem;

import java.util.List;

public class PacketDoGachaRsp extends BaseTypedPacket<DoGachaRsp> {
	public PacketDoGachaRsp(GachaBanner banner, List<GachaItem> list, PlayerGachaBannerInfo gachaInfo) {
        super(new DoGachaRsp());
		ItemParamData costItem = banner.getCost(1);
		ItemParamData costItem10 = banner.getCost(10);
		int gachaTimesLimit = banner.getGachaTimesLimit();
		int leftGachaTimes = switch(gachaTimesLimit) {
			case Integer.MAX_VALUE -> Integer.MAX_VALUE;
			default -> Math.max(gachaTimesLimit - gachaInfo.getTotalPulls(), 0);
		};
        proto.setGachaType(banner.getGachaType());
        proto.setGachaScheduleId(banner.getScheduleId());
        proto.setGachaTimes(list.size());
        proto.setNewGachaRandom(12345); //Chosen by fair dice roll. Guaranteed to be random.
        proto.setLeftGachaTimes(leftGachaTimes);
        proto.setGachaTimesLimit(gachaTimesLimit);
        proto.setCostItemId(costItem.getId());
        proto.setCostItemNum(costItem.getCount());
        proto.setTenCostItemId(costItem10.getId());
        proto.setTenCostItemNum(costItem10.getCount());
        proto.setGachaItemList(list);

		if(banner.hasEpitomized()) {
            proto.setWishItemId(gachaInfo.getWishItemId());
            proto.setWishProgress(gachaInfo.getFailedChosenItemPulls());
            proto.setWishMaxProgress(banner.getWishMaxProgress());
		}
	}

	public PacketDoGachaRsp() {
        super(new DoGachaRsp());
        proto.setRetcode(Retcode.RET_SVR_ERROR);
	}

	public PacketDoGachaRsp(Retcode retcode) {
        super(new DoGachaRsp());
        proto.setRetcode(retcode);
	}
}
