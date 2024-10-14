package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.SceneAreaUnlockNotify;

import java.util.Set;

public class PacketSceneAreaUnlockNotify extends BaseTypedPacket<SceneAreaUnlockNotify> {
    public PacketSceneAreaUnlockNotify(int sceneId, int areaId) {
        this(sceneId, Set.of(areaId));
    }

    public PacketSceneAreaUnlockNotify(int sceneId, Set<Integer> areaIds) {
        super(new SceneAreaUnlockNotify());
        proto.setSceneId(sceneId);
        proto.setAreaList(areaIds.stream().toList());
    }
}
