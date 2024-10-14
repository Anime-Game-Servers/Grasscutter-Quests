package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.scene.HomeSceneJumpRsp;

public class PacketHomeSceneJumpRsp extends BaseTypedPacket<HomeSceneJumpRsp> {
	public PacketHomeSceneJumpRsp(boolean enterRoomScene) {
        super(new HomeSceneJumpRsp());
        proto.setEnterRoomScene(enterRoomScene);
	}
}
