package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketTowerGetFloorStarRewardRsp;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.rotation.TowerGetFloorStarRewardReq;

public class HandlerTowerGetFloorStarRewardReq extends TypedPacketHandler<TowerGetFloorStarRewardReq> {
    @Override
    public void handle(GameSession session, byte[] header, TowerGetFloorStarRewardReq req) throws Exception {
        session.send(new PacketTowerGetFloorStarRewardRsp(
            session.getPlayer().getTowerManager().getStarReward(req.getFloorId()), req.getFloorId()));
    }
}
