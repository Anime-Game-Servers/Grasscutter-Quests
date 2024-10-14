package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.player.PlayerSetPauseRsp;

public class PacketPlayerSetPauseRsp extends BaseTypedPacket<PlayerSetPauseRsp> {

	public PacketPlayerSetPauseRsp() {
        super(new PlayerSetPauseRsp());
        proto.setRetcode(Retcode.RET_SUCC);
	}
}
