package emu.grasscutter.game.entity.gadget.platform;

import emu.grasscutter.game.world.Scene;
import emu.grasscutter.utils.Position;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.MathQuaternion;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.PlatformInfo;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGadget;

public abstract class BaseRoute {
    @Getter @Setter private boolean isStarted;
    @Getter @Setter private boolean isActive;
    @Getter @Setter private Position startRot;
    @Getter @Setter private int startSceneTime;
    @Getter @Setter private int stopSceneTime;
    @Getter @Setter private int startIndex;

    BaseRoute(Position startRot, boolean isStarted, boolean isActive) {
        this.startRot = startRot;
        this.isStarted = isStarted;
        this.isActive = isActive;
    }

    BaseRoute(SceneGadget gadget) {
        this.startRot = new Position(gadget.getRot());
        this.isStarted = false;
        this.isActive = true;
    }

    public static BaseRoute fromSceneGadget(SceneGadget sceneGadget) {
        if (sceneGadget.getRouteId() != 0) {
            return new ConfigRoute(sceneGadget);
        } else if (sceneGadget.isUsePointArray()) {
            return new PointArrayRoute(sceneGadget);
        }
        return null;
    }

    public boolean startRoute(Scene scene) {
        this.isStarted = true;
        this.isActive = true;
        this.startSceneTime = scene.getSceneTime();

        return true;
    }

    public boolean stopRoute(Scene scene) {
        this.isStarted = false;
        this.startSceneTime = scene.getSceneTime();
        this.stopSceneTime = scene.getSceneTime();

        return true;
    }

    private MathQuaternion rotAsMathQuaternion() {
        val result = new MathQuaternion();
        if (startRot != null) {
            // https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles
            // "MY BRAIN!" - Nazrin
            val roll = Math.toRadians(startRot.getX());
            val pitch = Math.toRadians(startRot.getY());
            val yaw = Math.toRadians(startRot.getZ());

            val cr = (float)Math.cos(roll * 0.5);
            val sr = (float)Math.sin(roll * 0.5);
            val cp = (float)Math.cos(pitch * 0.5);
            val sp = (float)Math.sin(pitch * 0.5);
            val cy = (float)Math.cos(yaw * 0.5);
            val sy = (float)Math.sin(yaw * 0.5);

            result.setW(cr * cp * cy + sr * sp * sy);
            result.setX(sr * cp * cy - cr * sp * sy);
            result.setY(cr * sp * cy + sr * cp * sy);
            result.setZ(cr * cp * sy - sr * sp * cy);
        } else {
            result.setW(1f);
        }
        return result;
    }

    public PlatformInfo toProto() {
        val result = new PlatformInfo();
        result.setStarted(isStarted);
        result.setActive(isActive);
        result.setStartRot(rotAsMathQuaternion());
        result.setStartSceneTime(startSceneTime);
        if (!isStarted) {
            result.setStopSceneTime(stopSceneTime);
        }
        result.setStartIndex(startIndex);
        return result;
    }
}
