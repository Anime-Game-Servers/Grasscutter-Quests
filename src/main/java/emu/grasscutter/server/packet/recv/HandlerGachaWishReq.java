package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.gacha.GachaBanner;
import emu.grasscutter.game.gacha.PlayerGachaBannerInfo;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGachaWishRsp;
import org.anime_game_servers.multi_proto.gi.messages.wishing.GachaWishReq;

public class HandlerGachaWishReq extends TypedPacketHandler<GachaWishReq> {
    @Override
    public void handle(GameSession session, byte[] header, GachaWishReq req) throws Exception {
        GachaBanner banner = session.getServer().getGachaSystem().getGachaBanners().get(req.getGachaScheduleId());
        PlayerGachaBannerInfo gachaInfo = session.getPlayer().getGachaInfo().getBannerInfo(banner);
        gachaInfo.setFailedChosenItemPulls(0);
        gachaInfo.setWishItemId(req.getItemId());
        session.send(new PacketGachaWishRsp(req.getGachaType(), req.getGachaScheduleId(), req.getItemId(), 0, banner.getWishMaxProgress()));
    }
}
