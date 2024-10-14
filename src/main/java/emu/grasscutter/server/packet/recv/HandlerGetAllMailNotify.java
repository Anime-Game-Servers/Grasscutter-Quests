package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetAllMailResultNotify;
import org.anime_game_servers.multi_proto.gi.messages.mail.GetAllMailNotify;

/**
 * Client request for the mail, used by 3.0 and higher to request the mail
 */
public class HandlerGetAllMailNotify extends TypedPacketHandler<GetAllMailNotify> {

    @Override
    public void handle(GameSession session, byte[] header, GetAllMailNotify req) throws Exception {
        session.send(new PacketGetAllMailResultNotify(session.getPlayer(), req.isCollected()));
    }
}
