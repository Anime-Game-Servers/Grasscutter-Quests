package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.home.HomeSceneItem;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.arangement.HomeGetArrangementInfoRsp;

import java.util.List;

public class PacketHomeGetArrangementInfoRsp extends BaseTypedPacket<HomeGetArrangementInfoRsp> {

	public PacketHomeGetArrangementInfoRsp(Player player, List<Integer> sceneIdList) {
        super(new HomeGetArrangementInfoRsp());
		var home = player.getHome();

		var homeScenes = sceneIdList.stream()
				.map(home::getHomeSceneItem)
				.map(HomeSceneItem::toProto)
				.toList();

		home.save();

        proto.setSceneArrangementInfoList(homeScenes);
	}
}
