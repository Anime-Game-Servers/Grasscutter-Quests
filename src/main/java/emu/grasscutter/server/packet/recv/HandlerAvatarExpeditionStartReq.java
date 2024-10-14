package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketAvatarExpeditionStartRsp;
import emu.grasscutter.utils.Utils;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.expedition.AvatarExpeditionStartReq;

public class HandlerAvatarExpeditionStartReq extends TypedPacketHandler<AvatarExpeditionStartReq> {
    @Override
    public void handle(GameSession session, byte[] header, AvatarExpeditionStartReq req) throws Exception {
        var player = session.getPlayer();

        int startTime = Utils.getCurrentSeconds();
        player.addExpeditionInfo(req.getAvatarGuid(), req.getExpId(), req.getHourTime(), startTime);
        player.save();
        session.send(new PacketAvatarExpeditionStartRsp(player.getExpeditionInfo()));
    }
}
