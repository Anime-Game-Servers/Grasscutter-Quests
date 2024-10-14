package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.world.WeatherArea.WeatherRefreshType;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSceneWeatherForcastRsp;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.scene.weather.SceneWeatherForecastReq;

public class HandlerSceneWeatherForcastReq extends TypedPacketHandler<SceneWeatherForecastReq> {
    @Override
    public void handle(GameSession session, byte[] header, SceneWeatherForecastReq req) throws Exception {
        val areaId = req.getWeatherAreaId();
        val player = session.getPlayer();
        val area = player.getScene().getWeatherAreas().get(areaId);
        if(area != null) {
            area.refresh(WeatherRefreshType.REFRESH_WEATHER_FORCAST);
            val forcast = area.getForcastList();
            session.getPlayer().sendPacket(new PacketSceneWeatherForcastRsp(forcast));
        } else {
            session.getPlayer().sendPacket(new PacketSceneWeatherForcastRsp(Retcode.RET_FAIL));
        }
    }
}
