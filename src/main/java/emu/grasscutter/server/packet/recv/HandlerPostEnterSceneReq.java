package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketPostEnterSceneRsp;

import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.scene.PostEnterSceneReq;

public class HandlerPostEnterSceneReq extends TypedPacketHandler<PostEnterSceneReq> {

    @Override
    public void handle(GameSession session, byte[] header, PostEnterSceneReq req) throws Exception {
        val player = session.getPlayer();
        val sceneId = player.getSceneId();
        val scene = player.getScene();
        val questManager = player.getQuestManager();
        switch (scene.getSceneType()){
            case SCENE_ROOM -> questManager.queueEvent(QuestContent.QUEST_CONTENT_ENTER_ROOM, sceneId,0);
            case SCENE_WORLD -> {
                questManager.queueEvent(QuestContent.QUEST_CONTENT_ENTER_MY_WORLD, sceneId);
                questManager.queueEvent(QuestContent.QUEST_CONTENT_ENTER_MY_WORLD_SCENE, sceneId);
            }
            case SCENE_DUNGEON -> {
                val dungeonManager = scene.getDungeonManager();
                if (dungeonManager != null) dungeonManager.startDungeon();
            }
        }

        if(scene.getPrevScene() != sceneId){
            questManager.queueEvent(QuestContent.QUEST_CONTENT_LEAVE_SCENE, scene.getPrevScene());
        }

        session.send(new PacketPostEnterSceneRsp(player));
        player.updateWeather(scene);
    }

}
