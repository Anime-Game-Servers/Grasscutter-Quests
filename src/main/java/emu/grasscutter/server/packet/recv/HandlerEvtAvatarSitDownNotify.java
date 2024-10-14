package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketEvtAvatarSitDownNotify;
import org.anime_game_servers.multi_proto.gi.messages.battle.event.EvtAvatarSitDownNotify;

public class HandlerEvtAvatarSitDownNotify extends TypedPacketHandler<EvtAvatarSitDownNotify> {

    @Override
    public void handle(GameSession session, byte[] header, EvtAvatarSitDownNotify notify) throws Exception {

        session.getPlayer().getScene().broadcastPacket(new PacketEvtAvatarSitDownNotify(notify));
    }

}

