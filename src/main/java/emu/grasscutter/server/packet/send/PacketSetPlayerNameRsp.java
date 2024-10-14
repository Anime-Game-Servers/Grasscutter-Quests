package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.player.SetPlayerNameRsp;

public class PacketSetPlayerNameRsp extends BaseTypedPacket<SetPlayerNameRsp> {
	public PacketSetPlayerNameRsp(Player player) {
        super(new SetPlayerNameRsp());
        proto.setNickName(player.getNickname());
	}
}
