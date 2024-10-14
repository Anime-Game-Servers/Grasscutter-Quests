package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.player.game_time.ClientLockGameTimeNotify;

public class HandlerClientLockGameTimeNotify extends TypedPacketHandler<ClientLockGameTimeNotify> {
	@Override
    public void handle(GameSession session, byte[] header, ClientLockGameTimeNotify req) throws Exception {
        Grasscutter.getLogger().error("LOCKING GAMETIME: {}", req.isLock());
		//session.getPlayer().getWorld().setGameTimeLocked(req.getIsLock());
	}
}
