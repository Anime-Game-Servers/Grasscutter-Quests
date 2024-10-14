package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketTowerBuffSelectRsp;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.run.TowerBuffSelectReq;

public class HandlerTowerBuffSelectReq extends TypedPacketHandler<TowerBuffSelectReq> {
    @Override
    public void handle(GameSession session, byte[] header, TowerBuffSelectReq req) throws Exception {
        session.getPlayer().getTowerManager().addBuffs(req.getTowerBuffId());
        session.send(new PacketTowerBuffSelectRsp(req.getTowerBuffId()));
    }
}
