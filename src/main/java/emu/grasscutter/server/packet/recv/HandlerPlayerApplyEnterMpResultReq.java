package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketPlayerApplyEnterMpResultRsp;
import org.anime_game_servers.multi_proto.gi.messages.multiplayer.PlayerApplyEnterMpResultReq;

public class HandlerPlayerApplyEnterMpResultReq extends TypedPacketHandler<PlayerApplyEnterMpResultReq> {
    @Override
    public void handle(GameSession session, byte[] header, PlayerApplyEnterMpResultReq req) throws Exception {
        session.getServer().getMultiplayerSystem().applyEnterMpReply(session.getPlayer(), req.getApplyUid(), req.isAgreed());
        session.send(new PacketPlayerApplyEnterMpResultRsp(req.getApplyUid(), req.isAgreed()));
    }
}
