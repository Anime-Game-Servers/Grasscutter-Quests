package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.PropChangeReason;
import org.anime_game_servers.multi_proto.gi.messages.player.PlayerPropChangeReasonNotify;

public class PacketPlayerPropChangeReasonNotify extends BaseTypedPacket<PlayerPropChangeReasonNotify> {
    public PacketPlayerPropChangeReasonNotify(Player player, PlayerProperty prop, int oldValue, int newValue, PropChangeReason changeReason) {
        super(new PlayerPropChangeReasonNotify());
        this.buildHeader(0);
        proto.setPropType(prop.getId());
        proto.setReason(changeReason);
        proto.setOldValue(oldValue);
        proto.setCurValue(newValue);
    }
}
