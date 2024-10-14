package emu.grasscutter.game.entity.gadget.platform;

import emu.grasscutter.utils.Position;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.MovingPlatformType;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.PlatformInfo;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGadget;

public class ConfigRoute extends BaseRoute {

    @Getter @Setter private int routeId;

    public ConfigRoute(SceneGadget gadget) {
        super(gadget);
        this.routeId = gadget.getRouteId();
    }

    public ConfigRoute(Position startRot, boolean startRoute, boolean isActive, int routeId) {
        super(startRot, startRoute, isActive);
        this.routeId = routeId;
    }

    @Override
    public PlatformInfo toProto() {
        val proto = super.toProto();
        proto.setRouteId(routeId);
        proto.setMovingPlatformType(MovingPlatformType.MOVING_PLATFORM_USE_CONFIG);
        return proto;
    }
}
