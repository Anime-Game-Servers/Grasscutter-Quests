package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import org.sorapointa.proto.AvatarDelNotifyOuterClass.AvatarDelNotify;

import java.util.List;

public class PacketAvatarDelNotify extends BasePacket {
	
	public PacketAvatarDelNotify(List<Long> avatarGuidList) {
		super(PacketOpcodes.AvatarDelNotify);

		AvatarDelNotify proto = AvatarDelNotify.newBuilder()
				.addAllAvatarGuidList(avatarGuidList)
				.build();

		this.setData(proto);
	}
}
