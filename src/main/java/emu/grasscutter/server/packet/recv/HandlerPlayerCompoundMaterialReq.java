package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.PlayerCompoundMaterialReq;

public class HandlerPlayerCompoundMaterialReq extends TypedPacketHandler<PlayerCompoundMaterialReq> {
    @Override
    public void handle(GameSession session, byte[] header, PlayerCompoundMaterialReq req) throws Exception {
        session.getPlayer().getCookingCompoundManager().handlePlayerCompoundMaterialReq(req);
    }

}
