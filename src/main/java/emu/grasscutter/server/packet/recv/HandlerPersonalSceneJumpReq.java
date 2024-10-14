package emu.grasscutter.server.packet.recv;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.ScenePointEntry;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketPersonalSceneJumpRsp;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.scene.PersonalSceneJumpReq;

import java.util.Optional;

public class HandlerPersonalSceneJumpReq extends TypedPacketHandler<PersonalSceneJumpReq> {

    @Override
    public void handle(GameSession session, byte[] header, PersonalSceneJumpReq req) throws Exception {
        val player = session.getPlayer();

        // get the scene point
        val pointData = Optional.ofNullable(GameData.getScenePointEntryById(player.getSceneId(), req.getPointId()))
            .map(ScenePointEntry::getPointData).orElse(null);

        if (pointData != null) {
            val pos = pointData.getTransPosWithFallback();
            val rot = pointData.getTransRotWithFallback();
            int sceneId = pointData.getTranSceneId();

            player.getWorld().transferPlayerToScene(player, sceneId, pos, rot);
            session.send(new PacketPersonalSceneJumpRsp(sceneId, pos));
        }

    }

}
