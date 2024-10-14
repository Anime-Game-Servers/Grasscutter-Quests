package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.AvatarTeamAllDataNotify;

import java.util.Map;
import java.util.stream.Collectors;

public class PacketAvatarTeamAllDataNotify extends BaseTypedPacket<AvatarTeamAllDataNotify> {
    public PacketAvatarTeamAllDataNotify(Player player) {
        super(new AvatarTeamAllDataNotify());

        // Add the id list for custom teams.
        proto.setBackupAvatarTeamOrderList(player.getTeamManager().getTeams().keySet().stream().filter(e -> e > 4).toList());

        // Add the avatar lists for all the teams the player has.
        proto.setAvatarTeamMap(player.getTeamManager().getTeams().entrySet()
            .stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toProto(player))));
    }
}
