package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.mail.DelMailReq;

public class HandlerDelMailReq extends TypedPacketHandler<DelMailReq> {

    @Override
    public void handle(GameSession session, byte[] header, DelMailReq req) throws Exception {
        session.getPlayer().getMailHandler().deleteMail(req.getMailIdList());
    }

}
