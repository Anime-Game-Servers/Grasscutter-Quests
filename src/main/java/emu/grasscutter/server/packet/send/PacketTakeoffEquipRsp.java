package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.avatar.TakeoffEquipRsp;

public class PacketTakeoffEquipRsp extends BaseTypedPacket<TakeoffEquipRsp> {
	public PacketTakeoffEquipRsp(long avatarGuid, int slot) {
        super(new TakeoffEquipRsp());
        proto.setAvatarGuid(avatarGuid);
        proto.setSlot(slot);
	}
}
