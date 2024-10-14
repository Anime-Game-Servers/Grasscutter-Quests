package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.tower.TowerData;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.run.TowerLevelStarCondData;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.run.TowerLevelStarCondNotify;

import java.util.stream.Stream;

public class PacketTowerLevelStarCondNotify extends BaseTypedPacket<TowerLevelStarCondNotify> {
    public PacketTowerLevelStarCondNotify(TowerData data) {
        super(new TowerLevelStarCondNotify());
        proto.setFloorId(data.getCurrentFloorId());
        proto.setLevelIndex(data.getCurrentLevelIndex());
        proto.setCondDataList(Stream.of(1, 2, 3)
            .map(i -> {
                val towerLevelStarCondData = new TowerLevelStarCondData();
                towerLevelStarCondData.setCondValue(i);
                return towerLevelStarCondData;
            }).toList());
    }
}
