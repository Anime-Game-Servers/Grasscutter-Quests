package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.SceneEntityDrownRsp;

public class PacketSceneEntityDrownRsp extends BaseTypedPacket<SceneEntityDrownRsp> {

    public PacketSceneEntityDrownRsp(int entityId) {
        super(new SceneEntityDrownRsp());
        proto.setEntityId(entityId);
    }
}


