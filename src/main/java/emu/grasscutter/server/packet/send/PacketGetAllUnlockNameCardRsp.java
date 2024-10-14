package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.community.player_presentation.GetAllUnlockNameCardRsp;

public class PacketGetAllUnlockNameCardRsp extends BaseTypedPacket<GetAllUnlockNameCardRsp> {
	public PacketGetAllUnlockNameCardRsp(Player player) {
        super(new GetAllUnlockNameCardRsp());
        proto.setNameCardList(player.getNameCardList().stream().toList());
	}
}
