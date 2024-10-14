package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetMailItemRsp;
import org.anime_game_servers.multi_proto.gi.messages.mail.GetMailItemReq;

public class HandlerGetMailItemReq extends TypedPacketHandler<GetMailItemReq> {
    @Override
    public void handle(GameSession session, byte[] header, GetMailItemReq req) throws Exception {
        session.send(new PacketGetMailItemRsp(session.getPlayer(), req.getMailIdList()));
    }

}
