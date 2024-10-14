package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.home.HomeBlockItem;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.mark_point.HomeMarkPointNotify;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.mark_point.HomeMarkPointSceneData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PacketHomeMarkPointNotify extends BaseTypedPacket<HomeMarkPointNotify> {

	public PacketHomeMarkPointNotify(Player player) {
        super(new HomeMarkPointNotify());

		if(player.getRealmList() == null){
			return;
		}

        List<HomeMarkPointSceneData> markPointDataList = new ArrayList<>();
		for(var moduleId : player.getRealmList()){
			var homeScene = player.getHome().getHomeSceneItem(moduleId + 2000);

            var markPointData = new HomeMarkPointSceneData();
            markPointData.setModuleId(moduleId);
            markPointData.setSceneId(moduleId + 2000);
            markPointData.setTeapotSpiritPos(homeScene.getDjinnPos().toProto());

			// Now it only supports the teleport point
			// TODO add more types
			var marks = homeScene.getBlockItems().values().stream()
					.map(HomeBlockItem::getDeployFurnitureList)
					.flatMap(Collection::stream)
					.filter(i -> i.getFurnitureId() == 373501)
					.map(x -> x.toMarkPointProto(3))
					.toList();

            markPointData.setFurnitureList(marks);
            markPointDataList.add(markPointData);
		}
        proto.setMarkPointDataList(markPointDataList);
	}
}
