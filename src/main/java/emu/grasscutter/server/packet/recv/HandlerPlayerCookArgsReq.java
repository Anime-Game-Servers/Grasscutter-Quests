package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.PlayerCookArgsReq;

public class HandlerPlayerCookArgsReq extends TypedPacketHandler<PlayerCookArgsReq> {
    @Override
    public void handle(GameSession session, byte[] header, PlayerCookArgsReq req) throws Exception {
        session.getPlayer().getCookingManager().handleCookArgsReq(req);
    }
}
