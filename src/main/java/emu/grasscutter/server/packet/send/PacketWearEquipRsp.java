package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.avatar.WearEquipRsp;

public class PacketWearEquipRsp extends BaseTypedPacket<WearEquipRsp> {
	public PacketWearEquipRsp(long avatarGuid, long equipGuid) {
        super(new WearEquipRsp());
        proto.setAvatarGuid(avatarGuid);
        proto.setEquipGuid(equipGuid);
	}
}
