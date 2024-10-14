package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.WorldPlayerReviveRsp;

public class PacketWorldPlayerReviveRsp extends BaseTypedPacket<WorldPlayerReviveRsp> {
	public PacketWorldPlayerReviveRsp() {
        super(new WorldPlayerReviveRsp());
	}
}
