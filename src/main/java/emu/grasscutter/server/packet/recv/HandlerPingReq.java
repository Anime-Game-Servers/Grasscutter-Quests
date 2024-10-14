package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketPingRsp;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.other.PingReq;
import org.anime_game_servers.multi_proto.gi.messages.packet_head.PacketHead;

public class HandlerPingReq extends TypedPacketHandler<PingReq> {

    @Override
    public void handle(GameSession session, byte[] header, PingReq req) throws Exception {
        val head = PacketHead.parseBy(header, session.getVersion());
        session.updateLastPingTime(req.getClientTime());
        session.send(new PacketPingRsp(head.getClientSequenceId(), req.getClientTime()));
    }

}
