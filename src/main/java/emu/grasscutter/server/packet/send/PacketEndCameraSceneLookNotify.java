package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.camera.EndCameraSceneLookNotify;

public class PacketEndCameraSceneLookNotify extends BaseTypedPacket<EndCameraSceneLookNotify> {
	public PacketEndCameraSceneLookNotify() {
        super(new EndCameraSceneLookNotify());
	}
}
