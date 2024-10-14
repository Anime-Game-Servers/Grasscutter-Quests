package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketPersonalLineAllDataRsp;
import org.anime_game_servers.multi_proto.gi.messages.quest.personal.PersonalLineAllDataReq;

public class HandlerPersonalLineAllDataReq extends TypedPacketHandler<PersonalLineAllDataReq> {
    @Override
    public void handle(GameSession session, byte[] header, PersonalLineAllDataReq req) throws Exception {
        session.send(new PacketPersonalLineAllDataRsp(session.getPlayer().getQuestManager().getMainQuests().values()));
    }
}
