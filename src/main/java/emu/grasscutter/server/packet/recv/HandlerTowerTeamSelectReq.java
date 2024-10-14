package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketTowerTeamSelectRsp;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.run.TowerTeamSelectReq;

public class HandlerTowerTeamSelectReq extends TypedPacketHandler<TowerTeamSelectReq> {
    @Override
    public void handle(GameSession session, byte[] header, TowerTeamSelectReq req) throws Exception {
        session.send(new PacketTowerTeamSelectRsp(
            session.getPlayer().getTowerManager().teamSelect(req.getFloorId(), req.getTowerTeamList())));
    }
}
