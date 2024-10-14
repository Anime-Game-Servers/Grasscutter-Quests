package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketChangeAvatarRsp;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.ChangeAvatarReq;

public class HandlerChangeAvatarReq extends TypedPacketHandler<ChangeAvatarReq> {

	@Override
    public void handle(GameSession session, byte[] header, ChangeAvatarReq req) throws Exception {
        session.getPlayer().sendPacket(new PacketChangeAvatarRsp(
            session.getPlayer().getTeamManager().changeAvatar(req.getGuid()),
            req.getGuid()
        ));
	}

}
