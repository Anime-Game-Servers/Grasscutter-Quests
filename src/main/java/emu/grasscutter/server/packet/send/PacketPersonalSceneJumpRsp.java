package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.utils.Position;
import org.anime_game_servers.multi_proto.gi.messages.scene.PersonalSceneJumpRsp;

public class PacketPersonalSceneJumpRsp extends BaseTypedPacket<PersonalSceneJumpRsp> {

	public PacketPersonalSceneJumpRsp(int sceneId, Position pos) {
        super(new PersonalSceneJumpRsp());
        proto.setDestSceneId(sceneId);
        proto.setDestPos(pos.toProto());
	}
}
