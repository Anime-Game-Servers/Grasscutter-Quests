package emu.grasscutter.server.packet.send;

import java.util.Collection;
import java.util.List;

import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.SceneEntityAppearNotify;
import org.anime_game_servers.multi_proto.gi.messages.scene.VisionType;

public class PacketSceneEntityAppearNotify extends BaseTypedPacket<SceneEntityAppearNotify> {

	public PacketSceneEntityAppearNotify(GameEntity entity) {
		super(new SceneEntityAppearNotify(VisionType.VISION_BORN), true);
        proto.setEntityList(List.of(entity.toProto()));
	}

	public PacketSceneEntityAppearNotify(GameEntity entity, VisionType vision, int param) {
		super(new SceneEntityAppearNotify(vision, param, List.of(entity.toProto())), true);
	}

	public PacketSceneEntityAppearNotify(Player player) {
		this(player.getTeamManager().getCurrentAvatarEntity());
	}

	public PacketSceneEntityAppearNotify(Collection<? extends GameEntity> entities, VisionType visionType) {
        super(new SceneEntityAppearNotify(visionType), true);

		proto.setEntityList(entities.stream().map(GameEntity::toProto).toList());
	}
}
