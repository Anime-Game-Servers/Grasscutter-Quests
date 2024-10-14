package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.entity.EntityMoveInfo;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.SceneEntityMoveNotify;

public class PacketSceneEntityMoveNotify extends BaseTypedPacket<SceneEntityMoveNotify> {
	public PacketSceneEntityMoveNotify(EntityMoveInfo moveInfo) {
        super(new SceneEntityMoveNotify(), true);
        proto.setMotionInfo(moveInfo.getMotionInfo());
        proto.setEntityId(moveInfo.getEntityId());
        proto.setSceneTime(moveInfo.getSceneTime());
        proto.setReliableSeq(moveInfo.getReliableSeq());
	}
}
