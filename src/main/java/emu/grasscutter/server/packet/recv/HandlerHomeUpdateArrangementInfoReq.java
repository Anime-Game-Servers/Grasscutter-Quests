package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketHomeUpdateArrangementInfoRsp;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.arangement.HomeUpdateArrangementInfoReq;

public class HandlerHomeUpdateArrangementInfoReq extends TypedPacketHandler<HomeUpdateArrangementInfoReq> {
	@Override
    public void handle(GameSession session, byte[] header, HomeUpdateArrangementInfoReq req) throws Exception {
        var homeScene = session.getPlayer().getHome().getHomeSceneItem(session.getPlayer().getSceneId());
		homeScene.update(req.getSceneArrangementInfo());
		session.getPlayer().getHome().save();
		session.send(new PacketHomeUpdateArrangementInfoRsp());
	}
}
