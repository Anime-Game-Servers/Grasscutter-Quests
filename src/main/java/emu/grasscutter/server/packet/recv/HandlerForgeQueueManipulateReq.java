package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.item.forge.ForgeQueueManipulateReq;

public class HandlerForgeQueueManipulateReq extends TypedPacketHandler<ForgeQueueManipulateReq> {
	@Override
    public void handle(GameSession session, byte[] header, ForgeQueueManipulateReq req) throws Exception {
		session.getPlayer().getForgingManager().handleForgeQueueManipulateReq(req);
	}
}
