package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketAvatarTeamAllDataNotify;
import emu.grasscutter.server.packet.send.PacketDelBackupAvatarTeamRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.team.DelBackupAvatarTeamReq;
import org.jetbrains.annotations.NotNull;

public class HandlerDelBackupAvatarTeamReq extends TypedPacketHandler<DelBackupAvatarTeamReq> {
    @Override
    public void handle(@NotNull GameSession session, byte[] header, DelBackupAvatarTeamReq req) throws Exception {
        boolean result = session.getPlayer().getTeamManager().removeCustomTeam(req.getBackupAvatarTeamId());
        // Send packets.
        if (result) {
            session.getPlayer().sendPacket(new PacketAvatarTeamAllDataNotify(session.getPlayer()));
        }
        session.getPlayer().sendPacket(new PacketDelBackupAvatarTeamRsp(
            result ? Retcode.RET_SUCC : Retcode.RET_FAIL,
            req.getBackupAvatarTeamId()));
    }
}
