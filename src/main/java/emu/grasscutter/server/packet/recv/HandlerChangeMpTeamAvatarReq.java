package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketChangeMpTeamAvatarRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.team.ChangeMpTeamAvatarReq;
import org.jetbrains.annotations.NotNull;

public class HandlerChangeMpTeamAvatarReq extends TypedPacketHandler<ChangeMpTeamAvatarReq> {
	@Override
    public void handle(@NotNull GameSession session, byte[] header, ChangeMpTeamAvatarReq req) throws Exception {
        boolean result = session.getPlayer().getTeamManager().setupMpTeam(req.getAvatarGuidList(), req.getCurAvatarGuid());
        new PacketChangeMpTeamAvatarRsp(req.getCurAvatarGuid(), req.getAvatarGuidList(),
            result ? Retcode.RET_SUCC : Retcode.RET_FAIL);
	}
}
