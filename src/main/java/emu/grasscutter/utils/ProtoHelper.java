package emu.grasscutter.utils;

import emu.grasscutter.game.props.PlayerProperty;
import org.anime_game_servers.multi_proto.gi.messages.general.PropValue;

public final class ProtoHelper {
	public static PropValue newPropValue(PlayerProperty key, int value) {
		return new PropValue(key.getId(), value, new PropValue.Value.Ival(value));
	}
}
