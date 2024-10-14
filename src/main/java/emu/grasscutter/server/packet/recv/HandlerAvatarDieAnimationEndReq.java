package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketAvatarDieAnimationEndRsp;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.AvatarDieAnimationEndReq;

public class HandlerAvatarDieAnimationEndReq extends TypedPacketHandler<AvatarDieAnimationEndReq> {
	@Override
    public void handle(GameSession session, byte[] header, AvatarDieAnimationEndReq req) throws Exception {
        // TODO There is also reborn pos being sent, which might be helpful
		session.getPlayer().getTeamManager().onAvatarDie(req.getDieGuid());
        // Response packet
        session.getPlayer().sendPacket(new PacketAvatarDieAnimationEndRsp(req.getDieGuid(), req.getSkillId()));
	}

}
