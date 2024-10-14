package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.item.management.ReliquaryDecomposeReq;

public class HandlerReliquaryDecomposeReq extends TypedPacketHandler<ReliquaryDecomposeReq> {
    @Override
    public void handle(GameSession session, byte[] header, ReliquaryDecomposeReq req) throws Exception {
        session.getServer().getCombineSystem().decomposeReliquaries(session.getPlayer(), req.getConfigId(), req.getTargetCount(), req.getGuidList());
    }
}
