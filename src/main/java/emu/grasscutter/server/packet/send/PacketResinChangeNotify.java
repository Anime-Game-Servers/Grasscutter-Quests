package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.ResinChangeNotify;

public class PacketResinChangeNotify extends BaseTypedPacket<ResinChangeNotify> {
    public PacketResinChangeNotify(Player player) {
        super(new ResinChangeNotify());
        proto.setCurValue(player.getProperty(PlayerProperty.PROP_PLAYER_RESIN));
        proto.setNextAddTimestamp(player.getNextResinRefresh());
        proto.setCurBuyCount(player.getResinBuyCount());
    }
}
