package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.scene.EnterTransPointRegionNotify;

public class HandlerEnterTransPointRegionNotify extends TypedPacketHandler<EnterTransPointRegionNotify> {
    @Override
    public void handle(GameSession session, byte[] header, EnterTransPointRegionNotify req) throws Exception {
        session.getPlayer().getSotsManager().handleEnterTransPointRegionNotify();
    }
}
