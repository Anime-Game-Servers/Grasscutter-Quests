package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.rotation.TowerGetFloorStarRewardRsp;

public class PacketTowerGetFloorStarRewardRsp extends BaseTypedPacket<TowerGetFloorStarRewardRsp> {
    public PacketTowerGetFloorStarRewardRsp(boolean result, int floorId) {
        super(new TowerGetFloorStarRewardRsp());
        proto.setFloorId(floorId);
        proto.setRetcode(result ? Retcode.RET_SUCC : Retcode.RET_SVR_ERROR);
    }
}
