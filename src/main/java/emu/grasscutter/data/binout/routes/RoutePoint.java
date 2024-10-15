package emu.grasscutter.data.binout.routes;

import emu.grasscutter.utils.Position;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.RoutePoint.MoveParams.Time;
import org.anime_game_servers.multi_proto.gi.messages.general.RoutePoint.MoveParams.Velocity;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoutePoint {
    private Position pos;
    private int speedLevel; //optional
    private float waitTime; //optional
    private float targetVelocity; //optional
    private boolean hasReachEvent; //optional
    private boolean reachStop; //optional
    // rotRoundReachDir //optional Pos with optional values
    // rotRoundLeaveDir //optional Pos with optional values

    public org.anime_game_servers.multi_proto.gi.messages.general.RoutePoint toProto(){
        val proto = new org.anime_game_servers.multi_proto.gi.messages.general.RoutePoint(pos.toProto());
        if(waitTime!=0){
            proto.setMoveParams(new Time(waitTime));
        } else if(targetVelocity!=0){
            proto.setMoveParams(new Velocity(targetVelocity));
        }

        return proto;
    }
}
