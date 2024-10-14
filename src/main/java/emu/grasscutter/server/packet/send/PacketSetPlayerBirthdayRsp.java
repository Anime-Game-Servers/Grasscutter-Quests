package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.community.player_presentation.SetPlayerBirthdayRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketSetPlayerBirthdayRsp extends BaseTypedPacket<SetPlayerBirthdayRsp> {

	public PacketSetPlayerBirthdayRsp(Retcode retCode) {
		super(new SetPlayerBirthdayRsp(retCode));
	}

	public PacketSetPlayerBirthdayRsp(Player player) {
		super(new SetPlayerBirthdayRsp(Retcode.RET_SUCC, player.getBirthday().toProto()));
	}
}
