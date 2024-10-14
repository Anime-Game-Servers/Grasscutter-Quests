package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.community.player_presentation.SetPlayerSignatureRsp;

public class PacketSetPlayerSignatureRsp extends BaseTypedPacket<SetPlayerSignatureRsp> {
	public PacketSetPlayerSignatureRsp(Player player) {
        super(new SetPlayerSignatureRsp());
        proto.setSignature(player.getSignature());
	}
}
