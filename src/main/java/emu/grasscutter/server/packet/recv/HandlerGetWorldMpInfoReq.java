package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetWorldMpInfoRsp;
import org.anime_game_servers.multi_proto.gi.messages.scene.GetWorldMpInfoReq;

public class HandlerGetWorldMpInfoReq extends TypedPacketHandler<GetWorldMpInfoReq> {
	@Override
    public void handle(GameSession session, byte[] header, GetWorldMpInfoReq req) throws Exception {
		session.send(new PacketGetWorldMpInfoRsp(session.getPlayer().getWorld()));
	}
}
