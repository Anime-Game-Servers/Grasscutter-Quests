package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetAllMailRsp;
import org.anime_game_servers.multi_proto.gi.messages.mail.GetAllMailReq;

/**
 * Client request for the mail, used by 2.8 and lower to request the mail
 */
public class HandlerGetAllMailReq extends TypedPacketHandler<GetAllMailReq> {

    @Override
    public void handle(GameSession session, byte[] header, GetAllMailReq req) throws Exception {
        session.send(new PacketGetAllMailRsp(session.getPlayer(), req.isCollected()));
    }
}
