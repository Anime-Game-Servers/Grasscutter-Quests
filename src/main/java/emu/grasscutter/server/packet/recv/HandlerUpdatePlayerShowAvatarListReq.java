package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketUpdatePlayerShowAvatarListRsp;
import org.anime_game_servers.multi_proto.gi.messages.community.player_presentation.UpdatePlayerShowAvatarListReq;

public class HandlerUpdatePlayerShowAvatarListReq extends TypedPacketHandler<UpdatePlayerShowAvatarListReq> {
    @Override
    public void handle(GameSession session, byte[] header, UpdatePlayerShowAvatarListReq req) throws Exception {
        session.getPlayer().setShowAvatars(req.isShowAvatar());
        session.getPlayer().setShowAvatarList(req.getShowAvatarIdList());
        session.send(new PacketUpdatePlayerShowAvatarListRsp(req.isShowAvatar(), req.getShowAvatarIdList()));
    }
}
