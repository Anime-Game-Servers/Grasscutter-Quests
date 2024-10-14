package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketChooseCurAvatarTeamRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.team.ChooseCurAvatarTeamReq;
import org.jetbrains.annotations.NotNull;

public class HandlerChooseCurAvatarTeamReq extends TypedPacketHandler<ChooseCurAvatarTeamReq> {
	@Override
    public void handle(@NotNull GameSession session, byte[] header, ChooseCurAvatarTeamReq req) throws Exception {
		boolean result = session.getPlayer().getTeamManager().setCurrentTeam(req.getTeamId());
        session.getPlayer().sendPacket(new PacketChooseCurAvatarTeamRsp(
            result ? Retcode.RET_SUCC : Retcode.RET_FAIL,
            req.getTeamId()));
	}
}
