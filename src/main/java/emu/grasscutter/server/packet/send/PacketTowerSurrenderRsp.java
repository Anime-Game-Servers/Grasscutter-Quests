package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.run.TowerSurrenderRsp;

public class PacketTowerSurrenderRsp extends BaseTypedPacket<TowerSurrenderRsp> {
    public PacketTowerSurrenderRsp() {
        super(new TowerSurrenderRsp());
    }
}
