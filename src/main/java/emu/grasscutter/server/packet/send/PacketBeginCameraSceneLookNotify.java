package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.utils.Position;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.anime_game_servers.multi_proto.gi.messages.scene.camera.BeginCameraSceneLookNotify;

import java.util.ArrayList;
import java.util.Collection;

public class PacketBeginCameraSceneLookNotify extends BaseTypedPacket<BeginCameraSceneLookNotify> {

	public PacketBeginCameraSceneLookNotify(CameraSceneLookNotify parameters) {
        super(new BeginCameraSceneLookNotify());
        proto.setLookPos(parameters.lookPos.toProto());
        proto.setFollowPos(parameters.followPos.toProto());
        proto.setDuration(parameters.duration);
        proto.setAllowInput(parameters.isAllowInput);
        proto.setSetFollowPos(parameters.setFollowPos);
        proto.setSetScreenXy(parameters.isScreenXY);
        proto.setRecoverKeepCurrent(parameters.recoverKeepCurrent);
        proto.setChangePlayMode(parameters.isChangePlayMode);
        proto.setScreenY(parameters.screenY);
        proto.setScreenX(parameters.screenX);
        proto.setForce(parameters.isForce);
        proto.setForceWalk(parameters.isForceWalk);
        proto.setEntityId(parameters.entityId);
        proto.setOtherParams(parameters.otherParams.stream().toList());
        //proto.setKeepRotType(); //KeepRotType
        //proto.setCustomRadius(); //Float
        //proto.setBlendType(); //Int
        //proto.setAbsFollowPos(); //Boolean
        //proto.setDisableProtect(); //Int
        //proto.setBlendDuration(); //Float
	}

    // TODO check default values
    // todo find missing field usages:
    //  enum Unk2700_HIAKNNCKHJB (Unk2700_LNCHDDOOECD)
    //  Unk3000_MNLLCJMPMNH (uint32)
    //  Unk2700_DHAHEKOGHBJ (float)
    //  Unk3000_IEFIKMHCKDH (uint32)
    //  Unk3000_OGCLMFFADBD (float)

    @Data @NoArgsConstructor
    public static class CameraSceneLookNotify{
        Position lookPos = new Position();
        Position followPos = new Position();
        float duration = 0.0f;
        boolean isAllowInput = true;
        boolean setFollowPos = false;
        boolean isScreenXY = false;
        boolean recoverKeepCurrent = true;
        boolean isForceWalk = false;
        boolean isForce = false;
        boolean isChangePlayMode = false;
        float screenY = 0.0f;
        float screenX = 0.0f;
        int entityId = 0;
        Collection<String> otherParams = new ArrayList<>(0);
    }
}
