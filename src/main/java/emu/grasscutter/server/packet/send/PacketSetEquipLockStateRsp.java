package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.management.SetEquipLockStateRsp;

public class PacketSetEquipLockStateRsp extends BaseTypedPacket<SetEquipLockStateRsp> {
	public PacketSetEquipLockStateRsp(GameItem equip) {
        super(new SetEquipLockStateRsp());
        proto.setTargetEquipGuid(equip.getGuid());
        proto.setLocked(equip.isLocked());
	}
}
