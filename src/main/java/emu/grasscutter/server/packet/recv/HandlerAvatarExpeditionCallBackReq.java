package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketAvatarExpeditionCallBackRsp;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.expedition.AvatarExpeditionCallBackReq;

public class HandlerAvatarExpeditionCallBackReq extends TypedPacketHandler<AvatarExpeditionCallBackReq> {
    @Override
    public void handle(GameSession session, byte[] header, AvatarExpeditionCallBackReq req) throws Exception {
        var player = session.getPlayer();

        for (int i = 0; i < req.getAvatarGuid().size(); i++) {
            player.removeExpeditionInfo(req.getAvatarGuid().get(i));
        }

        player.save();
        session.send(new PacketAvatarExpeditionCallBackRsp(player.getExpeditionInfo()));
    }
}
