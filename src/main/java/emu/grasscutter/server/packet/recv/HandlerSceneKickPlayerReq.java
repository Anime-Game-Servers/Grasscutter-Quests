package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSceneKickPlayerRsp;
import org.anime_game_servers.multi_proto.gi.messages.scene.SceneKickPlayerReq;

public class HandlerSceneKickPlayerReq extends TypedPacketHandler<SceneKickPlayerReq> {
    @Override
    public void handle(GameSession session, byte[] header, SceneKickPlayerReq req) throws Exception {
        if (session.getServer().getMultiplayerSystem().kickPlayer(session.getPlayer(), req.getTargetUid())) {
            // Success
            session.send(new PacketSceneKickPlayerRsp(req.getTargetUid()));
        } else {
            // Fail
            session.send(new PacketSceneKickPlayerRsp());
        }
    }
}
