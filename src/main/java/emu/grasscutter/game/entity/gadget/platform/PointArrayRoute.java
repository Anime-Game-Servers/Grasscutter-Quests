package emu.grasscutter.game.entity.gadget.platform;

import emu.grasscutter.utils.Position;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGadget;
import org.anime_game_servers.multi_proto.gi.messages.general.RoutePoint;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.MovingPlatformType;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.PlatformInfo;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.Route;

import java.util.ArrayList;
import java.util.List;

public class PointArrayRoute extends BaseRoute {

    @Getter @Setter int currentPoint;
    @Getter @Setter int pointArrayId;
    @Getter @Setter List<RoutePoint> routePoints;

    public PointArrayRoute(SceneGadget gadget) {
        super(gadget);
        routePoints = new ArrayList<>();
    }

    public PointArrayRoute(Position startRot, boolean startRoute, boolean isActive, int pointArrayId) {
        super(startRot, startRoute, isActive);
        this.pointArrayId = pointArrayId;
        routePoints = new ArrayList<>();
    }

    @Override
    public PlatformInfo toProto() {
        val proto = super.toProto();
        proto.setMovingPlatformType(MovingPlatformType.MOVING_PLATFORM_ROUTE);
        val routeInfo = new Route();
        routeInfo.setRoutePoints(routePoints);
        proto.setRoute(routeInfo);
        return proto;
    }
}
