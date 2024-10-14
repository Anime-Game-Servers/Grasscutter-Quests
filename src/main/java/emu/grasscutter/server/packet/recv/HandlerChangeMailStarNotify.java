package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.mail.Mail;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketMailChangeNotify;
import org.anime_game_servers.multi_proto.gi.messages.mail.ChangeMailStarNotify;

import java.util.ArrayList;
import java.util.List;

public class HandlerChangeMailStarNotify extends TypedPacketHandler<ChangeMailStarNotify> {

    @Override
    public void handle(GameSession session, byte[] header, ChangeMailStarNotify req) throws Exception {
        List<Mail> updatedMail = new ArrayList<>();

        for (int mailId : req.getMailIdList()) {
            Mail message = session.getPlayer().getMail(mailId);

            message.importance = req.isStar() ? 1 : 0;

            session.getPlayer().replaceMailByIndex(mailId, message);
            updatedMail.add(message);
        }

        session.send(new PacketMailChangeNotify(session.getPlayer(), updatedMail));
    }
}
