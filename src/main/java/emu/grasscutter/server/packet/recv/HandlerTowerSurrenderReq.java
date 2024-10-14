package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketTowerSurrenderRsp;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.run.TowerSurrenderReq;

public class HandlerTowerSurrenderReq extends TypedPacketHandler<TowerSurrenderReq> {
    @Override
    public void handle(GameSession session, byte[] header, TowerSurrenderReq req) throws Exception {
        session.getPlayer().getTowerManager().resetCurRecord();
        session.getPlayer().getTowerManager().notifyCurRecordChange();
        session.send(new PacketTowerSurrenderRsp());
    }
}
