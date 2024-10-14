package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetSceneAreaRsp;
import org.anime_game_servers.multi_proto.gi.messages.scene.GetSceneAreaReq;

public class HandlerGetSceneAreaReq extends TypedPacketHandler<GetSceneAreaReq> {

    @Override
    public void handle(GameSession session, byte[] header, GetSceneAreaReq req) throws Exception {
        session.send(new PacketGetSceneAreaRsp(session.getPlayer(), req.getSceneId()));
    }

}
