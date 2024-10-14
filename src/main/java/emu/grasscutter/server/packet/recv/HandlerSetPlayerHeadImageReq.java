package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSetPlayerHeadImageRsp;
import org.anime_game_servers.multi_proto.gi.messages.community.player_presentation.SetPlayerHeadImageReq;

public class HandlerSetPlayerHeadImageReq extends TypedPacketHandler<SetPlayerHeadImageReq> {
	@Override
    public void handle(GameSession session, byte[] header, SetPlayerHeadImageReq req) throws Exception {
        if (session.getPlayer().getAvatars().hasAvatar(req.getAvatarId())) {
            session.getPlayer().setHeadImage(req.getAvatarId());
			session.send(new PacketSetPlayerHeadImageRsp(session.getPlayer()));
		}
	}
}
