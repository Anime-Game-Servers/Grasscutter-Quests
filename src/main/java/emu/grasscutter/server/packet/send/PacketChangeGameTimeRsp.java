package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.player.game_time.ChangeGameTimeRsp;

public class PacketChangeGameTimeRsp extends BaseTypedPacket<ChangeGameTimeRsp> {

    public PacketChangeGameTimeRsp(Player player, int extraDays, boolean result) {
        super(new ChangeGameTimeRsp());

        //TODO handle result
        proto.setCurGameTime(player.getWorld().getGameTime());
        proto.setExtraDays(extraDays);
    }
}
