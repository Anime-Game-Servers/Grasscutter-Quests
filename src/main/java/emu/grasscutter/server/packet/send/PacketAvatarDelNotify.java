package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.AvatarDelNotify;

import java.util.List;

public class PacketAvatarDelNotify extends BaseTypedPacket<AvatarDelNotify> {
	public PacketAvatarDelNotify(List<Long> avatarGuidList) {
        super(new AvatarDelNotify());
        proto.setAvatarGuidList(avatarGuidList);
	}
}
