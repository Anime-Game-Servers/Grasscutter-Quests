package emu.grasscutter.server.packet.send;

import java.util.ArrayList;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.GetSceneAreaRsp;

public class PacketGetSceneAreaRsp extends BaseTypedPacket<GetSceneAreaRsp> {

    public PacketGetSceneAreaRsp(Player player, int sceneId) {
        super(new GetSceneAreaRsp());

        this.buildHeader(0);

        proto.setSceneId(sceneId);
        proto.setAreaIdList(new ArrayList<>(player.getUnlockedSceneAreas(sceneId)));

        proto.setCityInfoList(new ArrayList<>());
        GameData.getCityDataMap().values().stream().filter(cityData -> cityData.getSceneId() == sceneId).forEach(cityData -> {
            proto.getCityInfoList().add(player.getSotsManager().getCityInfo(cityData.getCityId()).toProto());
        });
    }
}
