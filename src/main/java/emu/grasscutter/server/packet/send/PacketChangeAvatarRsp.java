package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.ChangeAvatarRsp;

public class PacketChangeAvatarRsp extends BaseTypedPacket<ChangeAvatarRsp> {

	public PacketChangeAvatarRsp(Retcode ret, long guid) {
        super(new ChangeAvatarRsp());
        proto.setRetcode(ret);
        proto.setCurGuid(guid);

	}
}
