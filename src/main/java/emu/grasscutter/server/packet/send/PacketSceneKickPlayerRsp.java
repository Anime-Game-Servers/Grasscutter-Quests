package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.scene.SceneKickPlayerRsp;

public class PacketSceneKickPlayerRsp extends BaseTypedPacket<SceneKickPlayerRsp> {
	public PacketSceneKickPlayerRsp(int targetUid) {
        super(new SceneKickPlayerRsp());
        proto.setTargetUid(targetUid);
	}

	public PacketSceneKickPlayerRsp() {
        super(new SceneKickPlayerRsp());
        proto.setRetcode(Retcode.RET_SVR_ERROR);
	}
}
