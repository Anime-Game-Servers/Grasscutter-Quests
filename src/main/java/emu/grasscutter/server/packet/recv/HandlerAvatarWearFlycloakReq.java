package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketAvatarWearFlycloakRsp;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.cosmetic.AvatarWearFlycloakReq;

public class HandlerAvatarWearFlycloakReq extends TypedPacketHandler<AvatarWearFlycloakReq> {
	@Override
    public void handle(GameSession session, byte[] header, AvatarWearFlycloakReq req) throws Exception {
		boolean success = session.getPlayer().getAvatars().wearFlycloak(req.getAvatarGuid(), req.getFlycloakId());
        if (success) {
			session.getPlayer().sendPacket(new PacketAvatarWearFlycloakRsp(req.getAvatarGuid(), req.getFlycloakId()));
		} else {
			session.getPlayer().sendPacket(new PacketAvatarWearFlycloakRsp());
		}
	}
}
