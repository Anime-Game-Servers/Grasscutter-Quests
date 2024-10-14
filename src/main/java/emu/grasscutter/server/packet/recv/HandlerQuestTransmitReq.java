package emu.grasscutter.server.packet.recv;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.event.player.PlayerTeleportEvent.TeleportType;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketQuestTransmitRsp;
import emu.grasscutter.utils.Position;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.quest.entities.QuestTransmitReq;
import org.anime_game_servers.gi_lua.models.quest.QuestData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HandlerQuestTransmitReq extends TypedPacketHandler<QuestTransmitReq> {

    @Override
    public void handle(GameSession session, byte[] header, QuestTransmitReq req) throws Exception {
        val player = session.getPlayer();
        val posAndRot = new ArrayList<Position>();
        final int sceneId = Optional.ofNullable(GameData.getTeleportDataMap().get(req.getQuestId()))
            .map(QuestData::getTransmitPoints).stream().flatMap(List::stream).findFirst()
            .map(QuestData.TransmitPoint::getSceneId).orElse(3);

        val result = Optional.ofNullable(player.getQuestManager().getQuestById(req.getQuestId()))
            .map(GameQuest::getMainQuest).filter(mainQuest -> mainQuest.hasTeleportPostion(req.getQuestId(), posAndRot))
            .filter(mainQuest -> posAndRot.size() > 1) // make sure there is pos and rot
            .filter(mainQuest -> player.getWorld().transferPlayerToScene(
                player, sceneId, TeleportType.CLIENT, posAndRot.get(0), posAndRot.get(1)))
            .isPresent();

        session.send(new PacketQuestTransmitRsp(result, req));
    }
}
