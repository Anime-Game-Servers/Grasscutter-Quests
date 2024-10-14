package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.item.forge.ForgeStartReq;

public class HandlerForgeStartReq extends TypedPacketHandler<ForgeStartReq> {
    @Override
    public void handle(GameSession session, byte[] header, ForgeStartReq req) throws Exception {
        session.getPlayer().getForgingManager().handleForgeStartReq(req);
    }

}
