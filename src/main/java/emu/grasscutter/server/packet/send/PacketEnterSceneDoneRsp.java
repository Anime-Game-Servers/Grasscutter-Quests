package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.scene.EnterSceneDoneRsp;

public class PacketEnterSceneDoneRsp extends BaseTypedPacket<EnterSceneDoneRsp> {

	public PacketEnterSceneDoneRsp(Player player) {
		super(new EnterSceneDoneRsp(Retcode.RET_SUCC, player.getEnterSceneToken()));
	}
}
