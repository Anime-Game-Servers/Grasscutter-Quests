package emu.grasscutter.server.packet.send;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.community.GetOnlinePlayerListRsp;


public class PacketGetOnlinePlayerListRsp extends BaseTypedPacket<GetOnlinePlayerListRsp> {
    public PacketGetOnlinePlayerListRsp(Player session){
        super(new GetOnlinePlayerListRsp());

        val players = Grasscutter.getGameServer().getPlayers().values().stream()
            .limit(50)
            .filter(p-> p.getUid() != session.getUid())
            .map(Player::getOnlinePlayerInfo)
            .toList();
        proto.setPlayerInfoList(players);
    }
}
