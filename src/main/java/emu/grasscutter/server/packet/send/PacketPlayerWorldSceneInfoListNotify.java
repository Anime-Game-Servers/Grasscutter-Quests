package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.SceneType;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.PlayerWorldSceneInfoListNotify;
import org.anime_game_servers.multi_proto.gi.messages.scene.PlayerWorldSceneInfo;
import java.util.ArrayList;
import java.util.List;

public class PacketPlayerWorldSceneInfoListNotify extends BaseTypedPacket<PlayerWorldSceneInfoListNotify> {

    public PacketPlayerWorldSceneInfoListNotify(Player player) {
        super(new PlayerWorldSceneInfoListNotify());
        List<PlayerWorldSceneInfo> infoList = new ArrayList<>();

        // Iterate over all scenes
        for (var scene : GameData.getSceneDataMap().values()) {
            //only send big world info
            if (scene.getSceneType() != SceneType.SCENE_WORLD) continue;

            var worldInfoBuilder = new PlayerWorldSceneInfo();
            worldInfoBuilder.setSceneId(scene.getId());

            worldInfoBuilder.setSceneTagIdList(player.getSceneTagList(scene.getId()));
            infoList.add(worldInfoBuilder);
        }
        proto.setInfoList(infoList);
    }
}
