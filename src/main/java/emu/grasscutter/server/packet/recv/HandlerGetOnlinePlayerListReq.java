package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetOnlinePlayerListRsp;
import org.anime_game_servers.multi_proto.gi.messages.community.GetOnlinePlayerListReq;

public class HandlerGetOnlinePlayerListReq extends TypedPacketHandler<GetOnlinePlayerListReq> {
    @Override
    public void handle(GameSession session, byte[] header, GetOnlinePlayerListReq req) throws Exception {
        session.send(new PacketGetOnlinePlayerListRsp(session.getPlayer()));
    }
}
