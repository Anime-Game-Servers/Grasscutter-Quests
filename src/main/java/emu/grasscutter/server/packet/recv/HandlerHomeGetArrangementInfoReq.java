package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketHomeGetArrangementInfoRsp;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.arangement.HomeGetArrangementInfoReq;

public class HandlerHomeGetArrangementInfoReq extends TypedPacketHandler<HomeGetArrangementInfoReq> {
	@Override
    public void handle(GameSession session, byte[] header, HomeGetArrangementInfoReq req) throws Exception {
        session.send(new PacketHomeGetArrangementInfoRsp(session.getPlayer(), req.getSceneIdList()));
	}
}
