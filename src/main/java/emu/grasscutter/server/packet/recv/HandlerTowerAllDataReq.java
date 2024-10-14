package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketTowerAllDataRsp;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.rotation.TowerAllDataReq;

public class HandlerTowerAllDataReq extends TypedPacketHandler<TowerAllDataReq> {
    @Override
    public void handle(GameSession session, byte[] header, TowerAllDataReq req) throws Exception {
        session.send(new PacketTowerAllDataRsp(
            session.getServer().getTowerSystem(), session.getPlayer().getTowerManager()));
    }
}
