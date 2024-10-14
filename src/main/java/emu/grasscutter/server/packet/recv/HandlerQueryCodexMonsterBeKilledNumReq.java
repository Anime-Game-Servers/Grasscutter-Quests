package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketQueryCodexMonsterBeKilledNumRsp;
import org.anime_game_servers.multi_proto.gi.messages.codex.QueryCodexMonsterBeKilledNumReq;

public class HandlerQueryCodexMonsterBeKilledNumReq extends TypedPacketHandler<QueryCodexMonsterBeKilledNumReq> {
    @Override
    public void handle(GameSession session, byte[] header, QueryCodexMonsterBeKilledNumReq req) throws Exception {
        session.send(new PacketQueryCodexMonsterBeKilledNumRsp(session.getPlayer(), req.getCodexIdList()));
    }

}
