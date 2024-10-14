package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.player.PlayerPropChangeNotify;

public class PacketPlayerPropChangeNotify extends BaseTypedPacket<PlayerPropChangeNotify> {

    public PacketPlayerPropChangeNotify(Player player, PlayerProperty prop, int delta) {
        super(new PlayerPropChangeNotify());
        this.buildHeader(0);
        proto.setPropType(prop.getId());
        proto.setPropDelta(delta);
    }
}
