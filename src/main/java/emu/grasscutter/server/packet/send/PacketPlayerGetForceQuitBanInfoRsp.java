package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.player.PlayerGetForceQuitBanInfoRsp;

public class PacketPlayerGetForceQuitBanInfoRsp extends BaseTypedPacket<PlayerGetForceQuitBanInfoRsp> {
	public PacketPlayerGetForceQuitBanInfoRsp(Retcode retcode) {
        super(new PlayerGetForceQuitBanInfoRsp());
        proto.setRetcode(retcode);
	}
}
