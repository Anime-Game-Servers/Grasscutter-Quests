package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketHomeSceneInitFinishRsp;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.scene.HomeSceneInitFinishReq;

public class HandlerHomeSceneInitFinishReq extends TypedPacketHandler<HomeSceneInitFinishReq> {
	@Override
    public void handle(GameSession session, byte[] header, HomeSceneInitFinishReq req) throws Exception {
		session.send(new PacketHomeSceneInitFinishRsp());
	}
}
