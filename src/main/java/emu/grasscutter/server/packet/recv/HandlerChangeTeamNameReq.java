package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketChangeTeamNameRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.team.ChangeTeamNameReq;
import org.jetbrains.annotations.NotNull;

public class HandlerChangeTeamNameReq extends TypedPacketHandler<ChangeTeamNameReq> {
	@Override
    public void handle(@NotNull GameSession session, byte[] header, ChangeTeamNameReq req) throws Exception {
		boolean result = session.getPlayer().getTeamManager().setTeamName(req.getTeamId(), req.getTeamName());

        session.getPlayer().sendPacket(new PacketChangeTeamNameRsp(
            result ? Retcode.RET_SUCC : Retcode.RET_FAIL,
            req.getTeamId(), req.getTeamName()));
	}
}
