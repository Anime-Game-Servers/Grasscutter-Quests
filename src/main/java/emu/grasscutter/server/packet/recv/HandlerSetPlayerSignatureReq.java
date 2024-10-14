package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSetPlayerSignatureRsp;
import org.anime_game_servers.multi_proto.gi.messages.community.player_presentation.SetPlayerSignatureReq;

public class HandlerSetPlayerSignatureReq extends TypedPacketHandler<SetPlayerSignatureReq> {
	@Override
    public void handle(GameSession session, byte[] header, SetPlayerSignatureReq req) throws Exception {
        if (!req.getSignature().isEmpty()) {
			session.getPlayer().setSignature(req.getSignature());
			session.send(new PacketSetPlayerSignatureRsp(session.getPlayer()));
		}
	}
}
