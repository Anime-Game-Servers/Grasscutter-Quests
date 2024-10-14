package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.gadget.RequestLiveInfoRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketRequestLiveInfoRsp extends BaseTypedPacket<RequestLiveInfoRsp> {

	public PacketRequestLiveInfoRsp(int liveId, String liveUrl) {
		super(new RequestLiveInfoRsp(Retcode.RET_SUCC, liveId,liveUrl));
	}
}
