package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketUnlockTransPointRsp;
import org.anime_game_servers.multi_proto.gi.messages.scene.UnlockTransPointReq;

public class HandlerUnlockTransPointReq extends TypedPacketHandler<UnlockTransPointReq> {
    @Override
    public void handle(GameSession session, byte[] header, UnlockTransPointReq req) throws Exception {
        boolean unlocked = session.getPlayer().getProgressManager().unlockTransPoint(req.getSceneId(), req.getPointId(), false);
        session.getPlayer().sendPacket(new PacketUnlockTransPointRsp(unlocked));
    }
}
