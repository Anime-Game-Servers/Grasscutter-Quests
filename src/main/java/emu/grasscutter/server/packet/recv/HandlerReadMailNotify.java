package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.mail.Mail;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketMailChangeNotify;
import org.anime_game_servers.multi_proto.gi.messages.mail.ReadMailNotify;

import java.util.ArrayList;
import java.util.List;

public class HandlerReadMailNotify extends TypedPacketHandler<ReadMailNotify> {
    @Override
    public void handle(GameSession session, byte[] header, ReadMailNotify req) throws Exception {
        List<Mail> updatedMail = new ArrayList<>();

        for (int mailId : req.getMailIdList()) {
            Mail message = session.getPlayer().getMail(mailId);

            message.isRead = true;

            session.getPlayer().replaceMailByIndex(mailId, message);
            updatedMail.add(message);
        }

        session.send(new PacketMailChangeNotify(session.getPlayer(), updatedMail));
    }
}
