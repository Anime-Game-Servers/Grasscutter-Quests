package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.upgrade.ReliquaryUpgradeRsp;

import java.util.List;

public class PacketReliquaryUpgradeRsp extends BaseTypedPacket<ReliquaryUpgradeRsp> {
	public PacketReliquaryUpgradeRsp(GameItem relic, int rate, int oldLevel, List<Integer> oldAppendPropIdList) {
        super(new ReliquaryUpgradeRsp());
        proto.setTargetReliquaryGuid(relic.getGuid());
        proto.setOldLevel(oldLevel);
        proto.setCurLevel(relic.getLevel());
        proto.setPowerUpRate(rate);
        proto.setOldAppendPropList(oldAppendPropIdList);
        proto.setCurAppendPropList(relic.getAppendPropIdList());
	}
}
