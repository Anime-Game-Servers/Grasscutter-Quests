package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketAvatarChangeCostumeRsp;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.cosmetic.AvatarChangeCostumeReq;

public class HandlerAvatarChangeCostumeReq extends TypedPacketHandler<AvatarChangeCostumeReq> {

	@Override
	public void handle(GameSession session, byte[] header, AvatarChangeCostumeReq req) throws Exception {

		boolean success = session.getPlayer().getAvatars().changeCostume(req.getAvatarGuid(), req.getCostumeId());

		if (success) {
			session.getPlayer().sendPacket(new PacketAvatarChangeCostumeRsp(req.getAvatarGuid(), req.getCostumeId()));
		} else {
			session.getPlayer().sendPacket(new PacketAvatarChangeCostumeRsp());
		}
	}

}
