package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.home.HomeBlockItem;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.base_info.HomeComfortInfoNotify;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.module.HomeModuleComfortInfo;

import java.util.ArrayList;
import java.util.List;

public class PacketHomeComfortInfoNotify extends BaseTypedPacket<HomeComfortInfoNotify> {

    public PacketHomeComfortInfoNotify(Player player) {
        super(new HomeComfortInfoNotify());

        if (player.getRealmList() == null) {
            // Do not send
            return;
        }

        List<HomeModuleComfortInfo> comfortInfoList = new ArrayList<>();

        for (int moduleId : player.getRealmList()) {
            var homeScene = player.getHome().getHomeSceneItem(moduleId + 2000);
            var blockComfortList = homeScene.getBlockItems().values().stream()
                    .map(HomeBlockItem::calComfort)
                    .toList();
            var homeRoomScene = player.getHome().getHomeSceneItem(homeScene.getRoomSceneId());

            val homeModuleComfortInfo = new HomeModuleComfortInfo();
            homeModuleComfortInfo.setModuleId(moduleId);
            homeModuleComfortInfo.setRoomSceneComfortValue(homeRoomScene.calComfort());
            homeModuleComfortInfo.setWorldSceneBlockComfortValueList(blockComfortList);
            comfortInfoList.add(homeModuleComfortInfo);
        }

        proto.setModuleInfoList(comfortInfoList);
    }
}
