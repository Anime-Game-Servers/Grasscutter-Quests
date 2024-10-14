package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.tower.TowerFloorRecordInfo;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.rotation.TowerFloorRecordChangeNotify;

import java.util.List;

public class PacketTowerFloorRecordChangeNotify extends BaseTypedPacket<TowerFloorRecordChangeNotify> {
    public PacketTowerFloorRecordChangeNotify(List<TowerFloorRecordInfo> infoS, boolean canEnterScheduleFloor) {
        super(new TowerFloorRecordChangeNotify());
        proto.setTowerFloorRecordList(infoS.stream()
            .map(TowerFloorRecordInfo::toProto)
            .toList());
        proto.setFinishedEntranceFloor(canEnterScheduleFloor);
    }
}
