package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketHomeChangeBgmNotify;
import emu.grasscutter.server.packet.send.PacketHomeChangeBgmRsp;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.bgm.HomeChangeBgmReq;

public class HandlerHomeChangeBgmReq extends TypedPacketHandler<HomeChangeBgmReq> {
    @Override
    public void handle(GameSession session, byte[] header, HomeChangeBgmReq req) throws Exception {
        int homeBgmId = req.getBgmId();
        var home = session.getPlayer().getHome();

        home.getHomeSceneItem(session.getPlayer().getSceneId()).setHomeBgmId(homeBgmId);
        home.save();

        session.send(new PacketHomeChangeBgmNotify(homeBgmId));
        session.send(new PacketHomeChangeBgmRsp());
    }
}
