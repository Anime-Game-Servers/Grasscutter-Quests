package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.TowerFloorData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.tower.TowerFloorRecordInfo;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.rotation.TowerBriefDataNotify;

import java.util.Comparator;

public class PacketTowerBriefDataNotify extends BaseTypedPacket<TowerBriefDataNotify> {
    public PacketTowerBriefDataNotify(Player player) {
        super(new TowerBriefDataNotify());
        val towerManager = player.getTowerManager();
        val towerScheduleManager = player.getServer().getTowerSystem();
        val floorIndices = towerScheduleManager.getScheduleFloors().stream()
            .map(floorId -> GameData.getTowerFloorDataMap().get(floorId.intValue()))
            .map(TowerFloorData::getFloorIndex).filter(towerManager.getRecordMap()::containsKey).toList();
        val bestFloor = floorIndices.stream().max(Comparator.naturalOrder()).orElse(0);

        val bestFloorRecord = towerManager.getRecordMap().get(bestFloor);
        val lastLevelIndex = bestFloorRecord != null ? bestFloorRecord.getBestLevelIndex(): 0;

        proto.setTotalStarNum(floorIndices.stream().map(towerManager.getRecordMap()::get)
            .mapToInt(TowerFloorRecordInfo::getStarCount).sum());
        proto.setFinishedEntranceFloor(towerManager.canEnterScheduleFloor());
        proto.setScheduleStartTime(towerScheduleManager.getScheduleStartDate());
        proto.setLastFloorIndex(bestFloor);
        proto.setLastLevelIndex(lastLevelIndex);
        proto.setNextScheduleChangeTime(towerScheduleManager.getScheduleChangeDate());
        proto.setTowerScheduleId(towerScheduleManager.getCurrentTowerScheduleData().getScheduleId());
    }
}
