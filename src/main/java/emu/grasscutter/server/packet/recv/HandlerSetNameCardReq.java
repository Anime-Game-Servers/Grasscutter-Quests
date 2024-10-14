package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.community.player_presentation.SetNameCardReq;

public class HandlerSetNameCardReq extends TypedPacketHandler<SetNameCardReq> {
	@Override
    public void handle(GameSession session, byte[] header, SetNameCardReq req) throws Exception {
		session.getPlayer().setNameCard(req.getNameCardId());
	}
}
