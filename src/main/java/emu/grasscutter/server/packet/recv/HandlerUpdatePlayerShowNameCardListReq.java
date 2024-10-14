package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import org.anime_game_servers.multi_proto.gi.messages.community.player_presentation.UpdatePlayerShowNameCardListReq;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketUpdatePlayerShowNameCardListRsp;

public class HandlerUpdatePlayerShowNameCardListReq extends TypedPacketHandler<UpdatePlayerShowNameCardListReq> {
    @Override
    public void handle(GameSession session, byte[] header, UpdatePlayerShowNameCardListReq req) throws Exception {
        session.getPlayer().setShowNameCardList(req.getShowNameCardIdList());
        session.send(new PacketUpdatePlayerShowNameCardListRsp(req.getShowNameCardIdList()));
    }
}
