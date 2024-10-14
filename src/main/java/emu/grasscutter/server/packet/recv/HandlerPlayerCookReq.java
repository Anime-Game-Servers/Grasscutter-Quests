package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.PlayerCookReq;

public class HandlerPlayerCookReq extends TypedPacketHandler<PlayerCookReq> {
    @Override
    public void handle(GameSession session, byte[] header, PlayerCookReq req) throws Exception {
        session.getPlayer().getCookingManager().handlePlayerCookReq(req);
    }
}
