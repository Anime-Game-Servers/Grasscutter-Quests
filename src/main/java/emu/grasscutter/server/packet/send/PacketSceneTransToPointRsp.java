package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.scene.SceneTransToPointRsp;

public class PacketSceneTransToPointRsp extends BaseTypedPacket<SceneTransToPointRsp> {
    public PacketSceneTransToPointRsp(boolean result, int pointId, int sceneId) {
        super(new SceneTransToPointRsp());
        proto.setPointId(pointId);
        proto.setRetcode(result ? Retcode.RET_SUCC : Retcode.RET_SVR_ERROR);
        proto.setSceneId(sceneId);
    }
}
