package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketFireworksReformDataNotify;
import emu.grasscutter.server.packet.send.PacketReformFireworksRsp;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.firework.ReformFireworksReq;

public class HandlerReformFireworksReq extends TypedPacketHandler<ReformFireworksReq> {
    @Override
    public void handle(GameSession session, byte[] header, ReformFireworksReq req) throws Exception {
        session.send(new PacketFireworksReformDataNotify(req.getFireworksReformData()));
        session.send(new PacketReformFireworksRsp());
    }
}
