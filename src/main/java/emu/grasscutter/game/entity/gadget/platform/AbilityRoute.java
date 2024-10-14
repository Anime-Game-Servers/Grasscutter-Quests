package emu.grasscutter.game.entity.gadget.platform;

import emu.grasscutter.utils.Position;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.MathQuaternion;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.MovingPlatformType;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.PlatformInfo;

/**
 * TODO mostly hardcoded for EntitySolarIsotomaElevatorPlatform, should be more generic
 */
public class AbilityRoute extends BaseRoute {

    private final Position basePosition;

    public AbilityRoute(Position startRot, boolean startRoute, boolean isActive, Position basePosition) {
        super(startRot, startRoute, isActive);
        this.basePosition = basePosition;
    }

    @Override
    public PlatformInfo toProto() {
        val startRot = new MathQuaternion();
        startRot.setW(1.0F);
        val rotOffset = new MathQuaternion();
        rotOffset.setW(1.0F);
        val proto = super.toProto();
        proto.setStartRot(startRot);
        proto.setPosOffset(basePosition.toProto());
        proto.setRotOffset(rotOffset);
        proto.setMovingPlatformType(MovingPlatformType.MOVING_PLATFORM_ABILITY);
        return proto;
    }
}
