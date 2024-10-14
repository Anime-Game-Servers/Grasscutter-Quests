package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.tower.TowerManager;
import emu.grasscutter.game.tower.TowerSystem;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.rotation.TowerAllDataRsp;

import java.util.stream.Collectors;

public class PacketTowerAllDataRsp extends BaseTypedPacket<TowerAllDataRsp> {

    public PacketTowerAllDataRsp(TowerSystem towerSystem, TowerManager towerManager) {
        super(new TowerAllDataRsp());
        proto.setTowerScheduleId(towerSystem.getCurrentTowerScheduleData().getScheduleId());
        proto.setTowerFloorRecordList(towerManager.getTowerData().getTowerRecordProtoList());
        proto.setCurLevelRecord(towerManager.getCurRecordProto());
        proto.setScheduleStartTime(towerSystem.getScheduleStartDate());
        proto.setNextScheduleChangeTime(towerSystem.getScheduleChangeDate());
        proto.setFloorOpenTimeMap(towerSystem.getScheduleFloors().stream()
            .collect(Collectors.toMap(x -> x, y -> towerSystem.getScheduleStartDate())));
        proto.setFinishedEntranceFloor(towerManager.canEnterScheduleFloor());
        proto.setLastScheduleMonthlyBrief(towerManager.getMonthlyBriefInfo().toProto());
        proto.setMonthlyBrief(towerManager.getMonthlyBriefInfo().toProto());
//            .setIsFirstInteract(towerManager.getTowerData().getRecordMap().isEmpty())
    }
}
