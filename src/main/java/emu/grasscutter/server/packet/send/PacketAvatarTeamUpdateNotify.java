package emu.grasscutter.server.packet.send;

import java.util.Map.Entry;
import java.util.stream.Collectors;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.AvatarTeamUpdateNotify;

public class PacketAvatarTeamUpdateNotify extends BaseTypedPacket<AvatarTeamUpdateNotify> {

    public PacketAvatarTeamUpdateNotify(Player player) {
        super(new AvatarTeamUpdateNotify());

        if (player.getTeamManager().isUseTrialTeam()) {
            proto.setTempAvatarGuidList(player.getTeamManager().getActiveTeam().stream()
                .map(x -> x.getAvatar().getGuid()).toList());
        } else {
            proto.setAvatarTeamMap(player.getTeamManager().getTeams().entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, e->e.getValue().toProto(player))));
        }
    }

    public PacketAvatarTeamUpdateNotify() {
        // neccessary to unlock team modify if it was previously locked
        super(new AvatarTeamUpdateNotify());
    }
}
