package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.item.ItemParam;
import org.anime_game_servers.multi_proto.gi.messages.item.upgrade.WeaponUpgradeRsp;

import java.util.List;

public class PacketWeaponUpgradeRsp extends BaseTypedPacket<WeaponUpgradeRsp> {
	public PacketWeaponUpgradeRsp(GameItem item, int oldLevel, List<ItemParam> leftoverOres) {
        super(new WeaponUpgradeRsp());
        proto.setTargetWeaponGuid(item.getGuid());
        proto.setCurLevel(item.getLevel());
        proto.setOldLevel(oldLevel);
        proto.setItemParamList(leftoverOres);
	}
}
