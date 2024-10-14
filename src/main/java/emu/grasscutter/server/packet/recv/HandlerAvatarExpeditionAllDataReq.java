package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketAvatarExpeditionAllDataRsp;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.expedition.AvatarExpeditionAllDataReq;

public class HandlerAvatarExpeditionAllDataReq extends TypedPacketHandler<AvatarExpeditionAllDataReq> {
    @Override
    public void handle(GameSession session, byte[] header, AvatarExpeditionAllDataReq req) throws Exception {
        var player = session.getPlayer();
        session.send(new PacketAvatarExpeditionAllDataRsp(player.getExpeditionInfo(), player.getExpeditionLimit()));
    }
}
