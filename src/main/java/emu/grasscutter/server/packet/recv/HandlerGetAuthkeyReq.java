package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetAuthkeyRsp;
import org.anime_game_servers.multi_proto.gi.messages.mail.GetAuthkeyReq;

public class HandlerGetAuthkeyReq extends TypedPacketHandler<GetAuthkeyReq> {
	@Override
    public void handle(GameSession session, byte[] header, GetAuthkeyReq req) throws Exception {
		session.send(new PacketGetAuthkeyRsp());
	}
}
