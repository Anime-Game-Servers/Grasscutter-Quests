package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketAddBackupAvatarTeamRsp;
import emu.grasscutter.server.packet.send.PacketAvatarTeamAllDataNotify;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.AddBackupAvatarTeamReq;
import org.jetbrains.annotations.NotNull;

public class HandlerAddBackupAvatarTeamReq extends TypedPacketHandler<AddBackupAvatarTeamReq> {
    @Override
    public void handle(@NotNull GameSession session, byte[] header, AddBackupAvatarTeamReq req) throws Exception {
        boolean result = session.getPlayer().getTeamManager().addNewCustomTeam();
        // Send packets.
        if (result) {
            session.getPlayer().sendPacket(new PacketAvatarTeamAllDataNotify(session.getPlayer()));
        }
        session.getPlayer().sendPacket(new PacketAddBackupAvatarTeamRsp(result ? Retcode.RET_SUCC : Retcode.RET_FAIL));
    }
}
