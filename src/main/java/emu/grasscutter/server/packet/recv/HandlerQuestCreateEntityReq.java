package emu.grasscutter.server.packet.recv;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.GadgetData;
import emu.grasscutter.data.excels.ItemData;
import emu.grasscutter.data.excels.MonsterData;
import emu.grasscutter.game.entity.*;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketQuestCreateEntityRsp;
import emu.grasscutter.utils.Position;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.entity.CreateEntityInfo.Entity.GadgetId;
import org.anime_game_servers.multi_proto.gi.messages.general.entity.CreateEntityInfo.Entity.ItemId;
import org.anime_game_servers.multi_proto.gi.messages.general.entity.CreateEntityInfo.Entity.MonsterId;
import org.anime_game_servers.multi_proto.gi.messages.general.entity.CreateEntityInfo.Entity.NpcId;
import org.anime_game_servers.multi_proto.gi.messages.quest.entities.QuestCreateEntityReq;

public class HandlerQuestCreateEntityReq extends TypedPacketHandler<QuestCreateEntityReq> {

    @Override
    public void handle(GameSession session, byte[] header, QuestCreateEntityReq req) throws Exception {
        val entity = req.getEntity();
        val scene = session.getPlayer().getWorld().getSceneById(entity.getSceneId());

        val pos = new Position(entity.getPos());
        val rot = new Position(entity.getRot());
        GameEntity gameEntity = null;
        if (entity.getEntity() instanceof GadgetId gadgetId) {
            GadgetData gadgetData = GameData.getGadgetDataMap().get((int) gadgetId.getValue());
            gameEntity = switch (gadgetData.getType()) {
                case Vehicle -> new EntityVehicle(scene, session.getPlayer(), gadgetId.getValue(), 0, pos, rot);
                default -> new EntityGadget(scene, gadgetId.getValue(), pos, rot);
            };
        } else if (entity.getEntity() instanceof ItemId itemId) {
            ItemData itemData = GameData.getItemDataMap().get((int) itemId.getValue());
            gameEntity = new EntityItem(scene, null, itemData, pos, 1, true);
        } else if (entity.getEntity() instanceof MonsterId monsterId) {
            val level = entity.getLevel();
            MonsterData monsterData = GameData.getMonsterDataMap().get((int) monsterId.getValue());
            gameEntity = new EntityMonster(scene, monsterData, pos, level);
        } else if (entity.getEntity() instanceof NpcId npcId) {
            //nothing
        }

        if (gameEntity != null) {
            scene.addEntity(gameEntity);
        }

        val createdEntityId = gameEntity != null ? gameEntity.getId() : -1;

        session.send(new PacketQuestCreateEntityRsp(createdEntityId, req));
    }

}
