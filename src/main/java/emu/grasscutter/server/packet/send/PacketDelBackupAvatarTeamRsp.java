package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.team.DelBackupAvatarTeamRsp;

public class PacketDelBackupAvatarTeamRsp extends BaseTypedPacket<DelBackupAvatarTeamRsp> {
    public PacketDelBackupAvatarTeamRsp(Retcode retcode, int id) {
        super(new DelBackupAvatarTeamRsp());
        proto.setRetcode(retcode);
        proto.setBackupAvatarTeamId(id);
    }

    public PacketDelBackupAvatarTeamRsp(int id) {
        this(Retcode.RET_SUCC, id);
    }
}
