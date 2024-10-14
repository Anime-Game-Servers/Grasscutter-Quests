package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetGachaInfoRsp;
import org.anime_game_servers.multi_proto.gi.messages.wishing.GetGachaInfoReq;

public class HandlerGetGachaInfoReq extends TypedPacketHandler<GetGachaInfoReq> {
    @Override
    public void handle(GameSession session, byte[] header, GetGachaInfoReq req) throws Exception {
        session.send(new PacketGetGachaInfoRsp(session.getServer().getGachaSystem(), session.getPlayer()));
    }
}
