package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetFriendShowNameCardInfoRsp;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.GetFriendShowNameCardInfoReq;

public class HandlerGetFriendShowNameCardInfoReq extends TypedPacketHandler<GetFriendShowNameCardInfoReq> {
    @Override
    public void handle(GameSession session, byte[] header, GetFriendShowNameCardInfoReq req) throws Exception {
        Player target = session.getServer().getPlayerByUid(req.getUid(), true);
        session.send(new PacketGetFriendShowNameCardInfoRsp(req.getUid(), target.getShowNameCardInfoList()));
    }
}
