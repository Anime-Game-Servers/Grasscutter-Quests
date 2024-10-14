package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.scene.HitTreeNotify;

/**
 * Implement Deforestation Function
 */
public class HandlerHitTreeNotify extends TypedPacketHandler<HitTreeNotify> {
    @Override
    public void handle(GameSession session, byte[] header, HitTreeNotify req) throws Exception {
        session.getPlayer().getDeforestationManager().onDeforestationInvoke(req);
    }
}
