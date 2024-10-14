package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.battle.event.EvtAvatarSitDownNotify;

public class PacketEvtAvatarSitDownNotify extends BaseTypedPacket<EvtAvatarSitDownNotify> {

    public PacketEvtAvatarSitDownNotify(EvtAvatarSitDownNotify notify) {
        super(new EvtAvatarSitDownNotify(notify.getEntityId(), notify.getPosition(), notify.getChairId()));
    }
}
