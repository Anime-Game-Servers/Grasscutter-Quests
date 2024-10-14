package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.quest.variable.QuestUpdateQuestVarReq;
import org.anime_game_servers.multi_proto.gi.messages.quest.variable.QuestUpdateQuestVarRsp;

public class PacketQuestUpdateQuestVarRsp extends BaseTypedPacket<QuestUpdateQuestVarRsp> {

    public PacketQuestUpdateQuestVarRsp(QuestUpdateQuestVarReq req) {
        this(req, Retcode.RET_SUCC);
    }

    public PacketQuestUpdateQuestVarRsp(QuestUpdateQuestVarReq req, Retcode retcode) {
        super(new QuestUpdateQuestVarRsp());
        proto.setQuestId(req.getQuestId());
        proto.setParentQuestId(req.getParentQuestId());
        proto.setRetCode(retcode);
    }
}
