package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetScenePointRsp;
import org.anime_game_servers.multi_proto.gi.messages.scene.GetScenePointReq;

public class HandlerGetScenePointReq extends TypedPacketHandler<GetScenePointReq> {

    @Override
    public void handle(GameSession session, byte[] header, GetScenePointReq req) throws Exception {
        session.send(new PacketGetScenePointRsp(session.getPlayer(), req.getSceneId()));
    }
}
