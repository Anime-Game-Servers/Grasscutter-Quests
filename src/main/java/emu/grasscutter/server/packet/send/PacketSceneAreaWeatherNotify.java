package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.ClimateType;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.weather.SceneAreaWeatherNotify;

public class PacketSceneAreaWeatherNotify extends BaseTypedPacket<SceneAreaWeatherNotify> {

	public PacketSceneAreaWeatherNotify(Player player) {
		super(new SceneAreaWeatherNotify());

		proto.setWeatherAreaId(player.getWeatherId());
        proto.setClimateType(player.getClimate().getValue());
	}

	public PacketSceneAreaWeatherNotify(int areaId, ClimateType type, float transDuration) {
		super(new SceneAreaWeatherNotify());

		proto.setWeatherAreaId(areaId);
        proto.setClimateType(type.getValue());
		proto.setTransDuration(transDuration);
	}
}
