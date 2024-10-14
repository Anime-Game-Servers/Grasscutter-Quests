package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.upgrade.AvatarPromoteRsp;

public class PacketAvatarPromoteRsp extends BaseTypedPacket<AvatarPromoteRsp> {
	public PacketAvatarPromoteRsp(Avatar avatar) {
        super(new AvatarPromoteRsp());
        proto.setGuid(avatar.getGuid());
	}
}
