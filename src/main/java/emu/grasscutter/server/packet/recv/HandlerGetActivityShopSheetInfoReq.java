package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetActivityShopSheetInfoRsp;
import org.anime_game_servers.multi_proto.gi.messages.shop.GetActivityShopSheetInfoReq;

public class HandlerGetActivityShopSheetInfoReq extends TypedPacketHandler<GetActivityShopSheetInfoReq> {

    @Override
    public void handle(GameSession session, byte[] header, GetActivityShopSheetInfoReq req) throws Exception {
        session.getPlayer().sendPacket(new PacketGetActivityShopSheetInfoRsp(req.getShopType()));
    }

}
