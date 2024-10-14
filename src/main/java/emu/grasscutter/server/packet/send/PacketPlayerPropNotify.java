package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.utils.ProtoHelper;
import org.anime_game_servers.multi_proto.gi.messages.player.PlayerPropNotify;

import java.util.Map;

public class PacketPlayerPropNotify extends BaseTypedPacket<PlayerPropNotify> {

	public PacketPlayerPropNotify(Player player, PlayerProperty prop) {
		super(new PlayerPropNotify(Map.of(prop.getId(), ProtoHelper.newPropValue(prop, player.getProperty(prop)))));

		this.buildHeader(0);
	}
}
