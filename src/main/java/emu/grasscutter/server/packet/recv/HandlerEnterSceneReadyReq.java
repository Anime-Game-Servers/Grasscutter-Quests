package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketEnterScenePeerNotify;
import emu.grasscutter.server.packet.send.PacketEnterSceneReadyRsp;
import org.anime_game_servers.multi_proto.gi.messages.scene.EnterSceneReadyReq;

public class HandlerEnterSceneReadyReq extends TypedPacketHandler<EnterSceneReadyReq> {

	@Override
	public void handle(GameSession session, byte[] header, EnterSceneReadyReq req) {
        // This is how official behaves, don't remove, not sure if tower team also behaves the same way
        session.getPlayer().removeTrialAvatarForDungeon();
		session.send(new PacketEnterScenePeerNotify(session.getPlayer()));
		session.send(new PacketEnterSceneReadyRsp(session.getPlayer()));
	}

}
