package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.upgrade.WeaponPromoteRsp;

public class PacketWeaponPromoteRsp extends BaseTypedPacket<WeaponPromoteRsp> {
	public PacketWeaponPromoteRsp(GameItem item, int oldPromoteLevel) {
        super(new WeaponPromoteRsp());
        proto.setTargetWeaponGuid(item.getGuid());
        proto.setCurPromoteLevel(item.getPromoteLevel());
        proto.setOldPromoteLevel(oldPromoteLevel);
	}
}
