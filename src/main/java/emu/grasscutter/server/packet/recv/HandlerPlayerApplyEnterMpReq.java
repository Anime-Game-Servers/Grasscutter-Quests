package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketPlayerApplyEnterMpRsp;
import org.anime_game_servers.multi_proto.gi.messages.multiplayer.PlayerApplyEnterMpReq;

public class HandlerPlayerApplyEnterMpReq extends TypedPacketHandler<PlayerApplyEnterMpReq> {
    @Override
    public void handle(GameSession session, byte[] header, PlayerApplyEnterMpReq req) throws Exception {
        session.getServer().getMultiplayerSystem().applyEnterMp(session.getPlayer(), req.getTargetUid());
        session.send(new PacketPlayerApplyEnterMpRsp(req.getTargetUid()));
    }
}
