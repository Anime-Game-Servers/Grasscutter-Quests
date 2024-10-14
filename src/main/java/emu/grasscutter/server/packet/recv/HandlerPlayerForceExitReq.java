package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.player.PlayerForceExitReq;
import org.anime_game_servers.multi_proto.gi.messages.player.PlayerForceExitRsp;

public class HandlerPlayerForceExitReq extends TypedPacketHandler<PlayerForceExitReq> {
	@Override
    public void handle(GameSession session, byte[] header, PlayerForceExitReq req) throws Exception {
		// Client should auto disconnect right now
        session.send(new BaseTypedPacket<>(new PlayerForceExitRsp()) {
        });
		new Thread(){
			@Override
			public void run() {
				try {
					Thread.sleep(1000);// disconnect after 1 seconds
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				session.close();
				super.run();
			}
		}.start();
	}
}
