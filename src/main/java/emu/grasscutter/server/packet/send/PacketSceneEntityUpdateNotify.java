package emu.grasscutter.server.packet.send;

import java.util.Collection;
import java.util.List;

import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.SceneEntityUpdateNotify;
import org.anime_game_servers.multi_proto.gi.messages.scene.VisionType;

public class PacketSceneEntityUpdateNotify extends BaseTypedPacket<SceneEntityUpdateNotify> {

	public PacketSceneEntityUpdateNotify(GameEntity entity) {
		super(new SceneEntityUpdateNotify(), true);

        proto.setAppearType(VisionType.VISION_BORN);
		proto.setEntityList(List.of(entity.toProto()));
	}

	public PacketSceneEntityUpdateNotify(GameEntity entity, VisionType vision, int param) {
		super(new SceneEntityUpdateNotify(List.of(entity.toProto()), vision, param), true);
	}

	public PacketSceneEntityUpdateNotify(Player player) {
		this(player.getTeamManager().getCurrentAvatarEntity());
	}

	public PacketSceneEntityUpdateNotify(Collection<? extends GameEntity> entities, VisionType visionType) {
		super(new SceneEntityUpdateNotify(), true);

        proto.setAppearType(visionType);

		proto.setEntityList(entities.stream().map(GameEntity::toProto).toList());
	}
}
