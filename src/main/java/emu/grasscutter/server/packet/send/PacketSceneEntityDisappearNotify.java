package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.VisionType;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.SceneEntityDisappearNotify;

import java.util.Collection;
import java.util.List;


public class PacketSceneEntityDisappearNotify extends BaseTypedPacket<SceneEntityDisappearNotify> {

	public PacketSceneEntityDisappearNotify(GameEntity entity, VisionType disappearType) {
        super(new SceneEntityDisappearNotify());
        proto.setDisappearType(disappearType);
        proto.setEntityList(List.of(entity.getId()));
	}

	public PacketSceneEntityDisappearNotify(Collection<GameEntity> entities, VisionType disappearType) {
        super(new SceneEntityDisappearNotify());
        proto.setDisappearType(disappearType);
        proto.setEntityList(entities.stream().map(GameEntity::getId).toList());
	}
}
