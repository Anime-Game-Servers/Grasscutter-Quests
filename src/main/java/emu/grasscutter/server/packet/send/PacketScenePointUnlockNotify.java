package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.ScenePointUnlockNotify;

import java.util.ArrayList;
import java.util.List;

public class PacketScenePointUnlockNotify extends BaseTypedPacket<ScenePointUnlockNotify> {
    public PacketScenePointUnlockNotify(int sceneId, int pointId) {
        this(sceneId, pointId, false);
    }

    public PacketScenePointUnlockNotify(int sceneId, int pointId, boolean lock) {
        super(new ScenePointUnlockNotify());
        proto.setSceneId(sceneId);
        proto.setPointList(List.of(pointId));
        if (lock) {
            proto.setLockedPointList(List.of(pointId));
        } else {
            proto.setUnhidePointList(List.of(pointId));
        }
    }

    public PacketScenePointUnlockNotify(int sceneId, Iterable<Integer> pointIds) {
        this(sceneId, pointIds, false);
    }

    public PacketScenePointUnlockNotify(int sceneId, Iterable<Integer> pointIds, boolean lock) {
        super(new ScenePointUnlockNotify());
        proto.setSceneId(sceneId);
        List<Integer> pointIdsList = new ArrayList<>();
        pointIds.forEach(pointIdsList::add);
        proto.setPointList(pointIdsList);
        if (lock) {
            proto.setLockedPointList(pointIdsList);
        } else {
            proto.setUnhidePointList(pointIdsList);
        }
    }
}
