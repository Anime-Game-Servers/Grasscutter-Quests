package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.player.PlayerBuffManager.PlayerBuff;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.battle.ServerBuffChangeNotify;
import org.anime_game_servers.multi_proto.gi.messages.battle.ServerBuffChangeType;

import java.util.List;

public class PacketServerBuffChangeNotify extends BaseTypedPacket<ServerBuffChangeNotify> {
    public PacketServerBuffChangeNotify(Player player, ServerBuffChangeType changeType, PlayerBuff buff) {
        this(player, changeType, List.of(buff));
    }

    public PacketServerBuffChangeNotify(Player player, ServerBuffChangeType changeType, List<PlayerBuff> buffs) {
        super(new ServerBuffChangeNotify());
        proto.setAvatarGuidList(player.getTeamManager().getActiveTeam().stream()
            .map(entity -> entity.getAvatar().getGuid())
            .toList());
        proto.setServerBuffChangeType(changeType);
        proto.setServerBuffList(buffs.stream()
            .map(PlayerBuff::toProto)
            .toList());
    }
}
