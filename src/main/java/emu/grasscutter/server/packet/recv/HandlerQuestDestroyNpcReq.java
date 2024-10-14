package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketQuestDestroyNpcRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.quest.entities.QuestDestroyNpcReq;

public class HandlerQuestDestroyNpcReq extends TypedPacketHandler<QuestDestroyNpcReq> {
    @Override
    public void handle(GameSession session, byte[] header, QuestDestroyNpcReq req) throws Exception {
        session.send(new PacketQuestDestroyNpcRsp(req.getNpcId(), req.getParentQuestId(), Retcode.RET_SUCC));
    }
}
