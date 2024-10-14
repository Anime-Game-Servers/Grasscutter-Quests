package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.activity.water_spirit.region_search.GetRegionSearchReq;

public class HandlerGetRegionSearchReq extends TypedPacketHandler<GetRegionSearchReq> {
	@Override
    public void handle(GameSession session, byte[] header, GetRegionSearchReq req) throws Exception {
		// Auto template
	}
}
