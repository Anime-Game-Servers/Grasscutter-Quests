package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.scene.ExitTransPointRegionNotify;

public class HandlerExitTransPointRegionNotify extends TypedPacketHandler<ExitTransPointRegionNotify> {
    @Override
    public void handle(GameSession session, byte[] header, ExitTransPointRegionNotify req) throws Exception {
        session.getPlayer().getSotsManager().handleExitTransPointRegionNotify();
    }
}
