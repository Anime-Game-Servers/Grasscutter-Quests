package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSetUpAvatarTeamRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.SetUpAvatarTeamReq;
import org.jetbrains.annotations.NotNull;

public class HandlerSetUpAvatarTeamReq extends TypedPacketHandler<SetUpAvatarTeamReq> {
	@Override
    public void handle(@NotNull GameSession session, byte[] header, SetUpAvatarTeamReq req) throws Exception {
		boolean result = session.getPlayer().getTeamManager().setupAvatarTeam(
            req.getTeamId(), req.getAvatarTeamGuidList(), req.getCurAvatarGuid());

        session.getPlayer().sendPacket(new PacketSetUpAvatarTeamRsp(
            req.getTeamId(), req.getCurAvatarGuid(), req.getAvatarTeamGuidList(),
            result ? Retcode.RET_SUCC : Retcode.RET_FAIL));
	}
}
