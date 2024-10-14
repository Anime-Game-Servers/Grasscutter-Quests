package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketAddQuestContentProgressRsp;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.quest.child.AddQuestContentProgressReq;

public class HandlerAddQuestContentProgressReq extends TypedPacketHandler<AddQuestContentProgressReq> {
    @Override
    public void handle(GameSession session, byte[] header, AddQuestContentProgressReq req) throws Exception {
        //Find all conditions in quest that are the same as the given one
        val type = QuestContent.getContentTriggerByValue(req.getContentType());
        if(type!=null) {
            session.getPlayer().getQuestManager().queueEvent(type, req.getParam());
        }
        session.send(new PacketAddQuestContentProgressRsp(req.getContentType()));
    }
}
