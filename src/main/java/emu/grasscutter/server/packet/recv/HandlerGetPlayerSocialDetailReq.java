package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetPlayerSocialDetailRsp;
import org.anime_game_servers.multi_proto.gi.messages.community.player_presentation.GetPlayerSocialDetailReq;
import org.anime_game_servers.multi_proto.gi.messages.community.player_presentation.SocialDetail;

public class HandlerGetPlayerSocialDetailReq extends TypedPacketHandler<GetPlayerSocialDetailReq> {

	@Override
	public void handle(GameSession session, byte[] header, GetPlayerSocialDetailReq req) throws Exception {
		SocialDetail detail = session.getServer().getSocialDetailByUid(req.getUid());

		if (detail != null) {
			detail.setFriend(session.getPlayer().getFriendsList().isFriendsWith(req.getUid()));
		}

		session.send(new PacketGetPlayerSocialDetailRsp(detail));
	}
}
