package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketMusicGameStartRsp;
import org.anime_game_servers.multi_proto.gi.messages.activity.music_game.MusicGameStartReq;

public class HandlerMusicGameStartReq extends TypedPacketHandler<MusicGameStartReq> {

	@Override
	public void handle(GameSession session, byte[] header, MusicGameStartReq req) throws Exception {
		session.send(new PacketMusicGameStartRsp(req.getMusicBasicId(), req.getMusicBasicId()));
	}

}
