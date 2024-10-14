package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.team.ChangeTeamNameRsp;

public class PacketChangeTeamNameRsp extends BaseTypedPacket<ChangeTeamNameRsp> {
	public PacketChangeTeamNameRsp(Retcode ret, int teamId, String teamName) {
        super(new ChangeTeamNameRsp());
        proto.setTeamId(teamId);
        proto.setTeamName(teamName);
        proto.setRetcode(ret);
	}
}
