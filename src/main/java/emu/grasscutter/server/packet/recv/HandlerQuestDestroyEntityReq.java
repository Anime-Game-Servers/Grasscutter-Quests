package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketQuestDestroyEntityRsp;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.quest.entities.QuestDestroyEntityReq;

public class HandlerQuestDestroyEntityReq extends TypedPacketHandler<QuestDestroyEntityReq> {

    @Override
    public void handle(GameSession session, byte[] header, QuestDestroyEntityReq req) throws Exception {
        val scene = session.getPlayer().getWorld().getSceneById(req.getSceneId());
        val entity = scene.getEntityById(req.getEntityId());

        if(entity!=null){
            scene.removeEntity(entity);
        }

        session.send(new PacketQuestDestroyEntityRsp(entity!=null, req));
    }

}
