package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketQueryPathRsp;
import org.anime_game_servers.multi_proto.gi.messages.pathfinding.QueryPathReq;

public class HandlerQueryPathReq extends TypedPacketHandler<QueryPathReq> {
    @Override
    public void handle(GameSession session, byte[] header, QueryPathReq req) throws Exception {
        /**
         * It is not the actual work
         */

        if (!req.getDestinationPos().isEmpty()) {
            session.send(new PacketQueryPathRsp(req));
        }
    }
}
