package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketQuestUpdateQuestVarRsp;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.quest.variable.QuestUpdateQuestVarReq;
import org.anime_game_servers.multi_proto.gi.messages.quest.variable.QuestVarOp;

import java.util.List;

public class HandlerQuestUpdateQuestVarReq extends TypedPacketHandler<QuestUpdateQuestVarReq> {

    @Override
    public void handle(GameSession session, byte[] header, QuestUpdateQuestVarReq req) throws Exception {
        //Client sends packets. One with the value, and one with the index and the new value to set/inc/dec
        val questManager = session.getPlayer().getQuestManager();
        val subQuest = questManager.getQuestById(req.getQuestId());
        var mainQuest = questManager.getMainQuestById(req.getParentQuestId());
        if(mainQuest == null && subQuest!= null){
            mainQuest = subQuest.getMainQuest();
        }

        if (mainQuest==null){
            session.send(new PacketQuestUpdateQuestVarRsp(req, Retcode.RET_QUEST_NOT_EXIST));
            Grasscutter.getLogger().error("trying to update QuestVar for non existing quest s{} m{}", req.getQuestId(), req.getParentQuestId());
            return;
        }
        List<QuestVarOp> questVars = req.getQuestVarOpList();
        val questVarUpdate = mainQuest.getQuestVarsUpdate();
        if (questVarUpdate.size() == 0) {
            for (QuestVarOp questVar : questVars) {
                questVarUpdate.add(questVar.getValue());
            }
        } else {
            for (QuestVarOp questVar : questVars) {
                if (questVar.isAdd()) {
                    if (questVar.getValue() >= 0) {
                        mainQuest.incQuestVar(questVar.getIndex(), questVar.getValue());
                    } else {
                        mainQuest.decQuestVar(questVar.getIndex(), questVar.getValue());
                    }
                } else {
                    mainQuest.setQuestVar(questVar.getIndex(), questVarUpdate.get(0));
                }
                //remove the first element from the update list
                questVarUpdate.remove(0);
            }
        }
        session.send(new PacketQuestUpdateQuestVarRsp(req));
    }

}
