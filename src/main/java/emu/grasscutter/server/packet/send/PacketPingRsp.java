package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.other.PingRsp;

public class PacketPingRsp extends BaseTypedPacket<PingRsp> {

	public PacketPingRsp(int clientSeq, int time) {
        super(new PingRsp(), clientSeq);
        proto.setClientTime(time);
	}
}
