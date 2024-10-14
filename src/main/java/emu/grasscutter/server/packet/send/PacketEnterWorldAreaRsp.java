package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.EnterWorldAreaReq;
import org.anime_game_servers.multi_proto.gi.messages.scene.EnterWorldAreaRsp;

public class PacketEnterWorldAreaRsp extends BaseTypedPacket<EnterWorldAreaRsp> {

	public PacketEnterWorldAreaRsp(int clientSequence, EnterWorldAreaReq enterWorld) {
        super(new EnterWorldAreaRsp(), clientSequence);
        proto.setAreaType(enterWorld.getAreaType());
        proto.setAreaId(enterWorld.getAreaId());
	}
}
