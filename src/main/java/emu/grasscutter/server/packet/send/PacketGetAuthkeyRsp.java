package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.mail.GetAuthkeyRsp;

public class PacketGetAuthkeyRsp extends BaseTypedPacket<GetAuthkeyRsp> {
	public PacketGetAuthkeyRsp() {
        super(new GetAuthkeyRsp());
        proto.setRetCode(Retcode.RET_SVR_ERROR);
	}
}
