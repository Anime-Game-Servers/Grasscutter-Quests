package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.general.item.ItemParam;
import org.anime_game_servers.multi_proto.gi.messages.item.upgrade.CalcWeaponUpgradeReturnItemsRsp;

import java.util.List;

public class PacketCalcWeaponUpgradeReturnItemsRsp extends BaseTypedPacket<CalcWeaponUpgradeReturnItemsRsp> {
    public PacketCalcWeaponUpgradeReturnItemsRsp(long itemGuid, List<ItemParam> returnItems) {
        super(new CalcWeaponUpgradeReturnItemsRsp());
        proto.setTargetWeaponGuid(itemGuid);
        proto.setItemParamList(returnItems);
	}

    public PacketCalcWeaponUpgradeReturnItemsRsp() {
        super(new CalcWeaponUpgradeReturnItemsRsp());
        proto.setRetCode(Retcode.RET_SVR_ERROR);
	}
}
