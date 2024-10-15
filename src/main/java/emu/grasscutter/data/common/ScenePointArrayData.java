package emu.grasscutter.data.common;

import emu.grasscutter.utils.Position;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.RoutePoint;

@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings(value = "SpellCheckingInspection")
public class ScenePointArrayData {
    public int pointArrayId;
    public boolean isSimulateTempRouteByTime = false;
    public boolean isMoveToRoutePointOnInit = false;
    public PointArrayPoint[] platformPointList;

    @Data
    @EqualsAndHashCode(callSuper = false)
    @SuppressWarnings(value = "SpellCheckingInspection")
    public class PointArrayPoint {
        public int pointId;
        public Position position;
        public Position rotation;
        public float velocity;
        public float time;
        public boolean isReachEvent;
        public Position rotAxis;
        public float rotSpeed;
        public float arriveRange;

        public RoutePoint toProto() {
            val proto = new RoutePoint();
            proto.setArriveRange(arriveRange);
            proto.setPosition(position.toProto());

            if (rotSpeed == 0.0f) {
                proto.setRotateParams(new RoutePoint.RotateParams.Rotation(rotation.toProto()));
            }

            if (velocity > time) {
                proto.setMoveParams(new RoutePoint.MoveParams.Velocity(velocity));
            } else {
                proto.setMoveParams(new RoutePoint.MoveParams.Time(time));
            }

            return proto;
        }
    }
}
