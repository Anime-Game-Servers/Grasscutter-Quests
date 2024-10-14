package emu.grasscutter.server.event.entity;

import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.server.event.types.EntityEvent;
import emu.grasscutter.utils.Position;
import lombok.Getter;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.MotionState;

import javax.annotation.Nullable;

public final class EntityMoveEvent extends EntityEvent {
    @Getter @Nullable
    private final Position position, rotation, speed;
    @Getter
    private final MotionState motionState;

    public EntityMoveEvent(GameEntity entity, @Nullable Position position, @Nullable Position rotation, @Nullable Position speed, MotionState motionState) {
        super(entity);

        this.position = position;
        this.rotation = rotation;
        this.speed = speed;
        this.motionState = motionState;
    }
}
