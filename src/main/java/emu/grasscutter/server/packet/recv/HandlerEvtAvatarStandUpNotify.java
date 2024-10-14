package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketEvtAvatarStandUpNotify;
import org.anime_game_servers.multi_proto.gi.messages.battle.event.EvtAvatarStandUpNotify;

public class HandlerEvtAvatarStandUpNotify extends TypedPacketHandler<EvtAvatarStandUpNotify> {

    @Override
    public void handle(GameSession session, byte[] header, EvtAvatarStandUpNotify notify) throws Exception {

        session.getPlayer().getScene().broadcastPacket(new PacketEvtAvatarStandUpNotify(notify));
    }

}
