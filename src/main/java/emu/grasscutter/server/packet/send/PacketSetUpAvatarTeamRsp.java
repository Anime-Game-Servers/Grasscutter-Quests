package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.SetUpAvatarTeamRsp;

import java.util.List;

public class PacketSetUpAvatarTeamRsp extends BaseTypedPacket<SetUpAvatarTeamRsp> {
    public PacketSetUpAvatarTeamRsp(int teamId, long guid, List<Long> guidList, Retcode retVal) {
        super(new SetUpAvatarTeamRsp());
        proto.setTeamId(teamId);
        proto.setCurAvatarGuid(guid);
        proto.setAvatarTeamGuidList(guidList);
        proto.setRetcode(retVal);
    }
}
