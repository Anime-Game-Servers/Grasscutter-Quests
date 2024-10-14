package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketPlayerSetPauseRsp;
import org.anime_game_servers.multi_proto.gi.messages.player.PlayerSetPauseReq;

public class HandlerPlayerSetPauseReq extends TypedPacketHandler<PlayerSetPauseReq> {

	@Override
    public void handle(GameSession session, byte[] header, PlayerSetPauseReq req) throws Exception {
        session.getPlayer().getWorld().setPaused(req.isPaused());
		session.send(new PacketPlayerSetPauseRsp());
	}

}
