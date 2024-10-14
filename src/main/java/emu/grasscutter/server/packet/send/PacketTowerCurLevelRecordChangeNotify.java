package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.rotation.TowerCurLevelRecord;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.rotation.TowerCurLevelRecordChangeNotify;

public class PacketTowerCurLevelRecordChangeNotify extends BaseTypedPacket<TowerCurLevelRecordChangeNotify> {
    public PacketTowerCurLevelRecordChangeNotify(TowerCurLevelRecord recordProto) {
        super(new TowerCurLevelRecordChangeNotify());
        proto.setCurLevelRecord(recordProto);
    }
}
