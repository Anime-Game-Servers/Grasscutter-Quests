package emu.grasscutter.server.packet.recv;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.ScenePointEntry;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.event.player.PlayerTeleportEvent.TeleportType;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSceneTransToPointRsp;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.scene.SceneTransToPointReq;

import java.util.Optional;

public class HandlerSceneTransToPointReq extends TypedPacketHandler<SceneTransToPointReq> {

    @Override
    public void handle(GameSession session, byte[] header, SceneTransToPointReq req) throws Exception {
        val player = session.getPlayer();
        val result = Optional.ofNullable(GameData.getScenePointEntryById(req.getSceneId(), req.getPointId()))
            .map(ScenePointEntry::getPointData)
            .filter(pointData -> player.getWorld().transferPlayerToScene(
                player, req.getSceneId(), TeleportType.WAYPOINT,
                pointData.getTransPosWithFallback(), pointData.getTransRotWithFallback()))
            .isPresent();

        session.send(new PacketSceneTransToPointRsp(result, req.getPointId(), req.getSceneId()));
    }

}
