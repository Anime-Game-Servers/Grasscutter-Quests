package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.scene.CityInfo;
import org.anime_game_servers.multi_proto.gi.messages.scene.LevelupCityRsp;

public class PacketLevelupCityRsp extends BaseTypedPacket<LevelupCityRsp> {

    public PacketLevelupCityRsp(
            int sceneId, int level, int cityId, int crystalNum, int areaId, Retcode retcode) {
        super(new LevelupCityRsp());

        proto.setSceneId(sceneId);
        proto.setCityInfo(new CityInfo(cityId, crystalNum, level));
        proto.setAreaId(areaId);
        proto.setRetcode(retcode);
    }
}
