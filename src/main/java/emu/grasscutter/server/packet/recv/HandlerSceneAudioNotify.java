package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSceneAudioNotify;
import org.anime_game_servers.multi_proto.gi.messages.scene.SceneAudioNotify;

public class HandlerSceneAudioNotify extends TypedPacketHandler<SceneAudioNotify> {
	@Override
    public void handle(GameSession session, byte[] header, SceneAudioNotify req) throws Exception {
        session.getPlayer().getScene().broadcastPacket(new PacketSceneAudioNotify(req));
	}
}
