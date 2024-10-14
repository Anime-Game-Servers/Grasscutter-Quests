package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketPlayerHomeCompInfoNotify;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.comp_info.GetPlayerHomeCompInfoReq;

public class HandlerGetPlayerHomeCompInfoReq extends TypedPacketHandler<GetPlayerHomeCompInfoReq> {
	@Override
    public void handle(GameSession session, byte[] header, GetPlayerHomeCompInfoReq req) throws Exception {
		session.send(new PacketPlayerHomeCompInfoNotify(session.getPlayer()));
	}
}
