package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.player.TeamInfo;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.team.ChangeMpTeamAvatarRsp;

import java.util.List;

public class PacketChangeMpTeamAvatarRsp extends BaseTypedPacket<ChangeMpTeamAvatarRsp> {
	public PacketChangeMpTeamAvatarRsp(Player player, TeamInfo teamInfo) {
        super(new ChangeMpTeamAvatarRsp());
        proto.setCurAvatarGuid(player.getTeamManager().getCurrentCharacterGuid());
        proto.setAvatarGuidList(teamInfo.getAvatars().stream()
            .map(avatarId -> player.getAvatars().getAvatarById(avatarId).getGuid())
            .toList());
	}

    public PacketChangeMpTeamAvatarRsp(long playerGuid, List<Long> teamGuidList, Retcode retCode) {
        super(new ChangeMpTeamAvatarRsp());
        proto.setCurAvatarGuid(playerGuid);
        proto.setAvatarGuidList(teamGuidList);
        proto.setRetcode(retCode);
    }
}
