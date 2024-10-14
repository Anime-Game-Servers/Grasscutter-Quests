package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.scene.PostEnterSceneRsp;

public class PacketPostEnterSceneRsp extends BaseTypedPacket<PostEnterSceneRsp> {

	public PacketPostEnterSceneRsp(Player player) {
		super(new PostEnterSceneRsp(Retcode.RET_SUCC, player.getEnterSceneToken()));
	}
}
