package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.SceneAudioNotify;

import java.util.List;

public class PacketSceneAudioNotify extends BaseTypedPacket<SceneAudioNotify> {
    public PacketSceneAudioNotify(SceneAudioNotify notify) {
        super(notify);
    }

    public PacketSceneAudioNotify(List<Integer> param1, List<Float> param2, List<String> param3, int sourceUid, int type) {
        super(new SceneAudioNotify());
        proto.setParam1(param1);
        proto.setParam2(param2);
        proto.setParam3(param3);
        proto.setSourceUid(sourceUid);
        proto.setType(type);
    }
}
