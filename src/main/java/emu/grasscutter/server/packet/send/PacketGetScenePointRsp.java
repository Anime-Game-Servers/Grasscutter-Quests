package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.scene.GetScenePointRsp;

import java.util.HashSet;
import java.util.Set;

public class PacketGetScenePointRsp extends BaseTypedPacket<GetScenePointRsp> {
    private static final Set<Integer> bruteForceScenePointIdList = buildBruteForceScenePointIdList();

    public PacketGetScenePointRsp(Player player, int sceneId) {
        super(new GetScenePointRsp());
        val unlockedAreaList = player.getUnlockedSceneAreas(sceneId);
        val scenePointIdList = GameData.getScenePointIdList().isEmpty() ? bruteForceScenePointIdList :
            player.getUnlockedScenePoints(sceneId);
        proto.setSceneId(sceneId);
        proto.setUnhidePointList(scenePointIdList.stream().toList());
        proto.setUnlockedPointList(scenePointIdList.stream().toList());
        proto.setUnlockAreaList(unlockedAreaList.stream().toList());
        ;
    }

    private static Set<Integer> buildBruteForceScenePointIdList() {
        val set = new HashSet<Integer>(1000);
        for (int i = 1; i < 1000; i++) {
            set.add(i);
        }

        return set;
    }
}
