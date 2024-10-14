package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.base_info.HomeBasicInfo;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.base_info.HomeBasicInfoNotify;

public class PacketHomeBasicInfoNotify extends BaseTypedPacket<HomeBasicInfoNotify> {

    public PacketHomeBasicInfoNotify(Player player, boolean editMode) {
        super(new HomeBasicInfoNotify());
        if (player.getCurrentRealmId() <= 0) {
            return;
        }
        var sceneId = player.getCurrentRealmId() + 2000;
        var homeScene = player.getHome().getHomeSceneItem(sceneId);
        val homeBasicInfo = new HomeBasicInfo();
        homeBasicInfo.setCurModuleId(player.getCurrentRealmId());
        homeBasicInfo.setCurRoomSceneId(homeScene.getRoomSceneId());
        homeBasicInfo.setInEditMode(editMode);
        homeBasicInfo.setHomeOwnerUid(player.getUid());
        homeBasicInfo.setLevel(player.getHome().getLevel());
        homeBasicInfo.setOwnerNickName(player.getNickname());
                // TODO limit shop
        proto.setBasicInfo(homeBasicInfo);
    }
}
