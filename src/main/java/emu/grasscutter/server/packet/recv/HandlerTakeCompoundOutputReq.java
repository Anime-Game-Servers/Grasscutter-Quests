package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.TakeCompoundOutputReq;

public class HandlerTakeCompoundOutputReq extends TypedPacketHandler<TakeCompoundOutputReq> {
    @Override
    public void handle(GameSession session, byte[] header, TakeCompoundOutputReq req) throws Exception {
        session.getPlayer().getCookingCompoundManager().handleTakeCompoundOutputReq(req);
    }
}
