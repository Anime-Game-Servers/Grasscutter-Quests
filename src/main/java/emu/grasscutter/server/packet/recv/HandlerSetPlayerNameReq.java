package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSetPlayerNameRsp;
import org.anime_game_servers.multi_proto.gi.messages.player.SetPlayerNameReq;

public class HandlerSetPlayerNameReq extends TypedPacketHandler<SetPlayerNameReq> {
	@Override
    public void handle(GameSession session, byte[] header, SetPlayerNameReq req) throws Exception {
		// Auto template
        if (!req.getNickName().isEmpty()) {
			session.getPlayer().setNickname(req.getNickName());
			session.send(new PacketSetPlayerNameRsp(session.getPlayer()));
		}
	}
}
