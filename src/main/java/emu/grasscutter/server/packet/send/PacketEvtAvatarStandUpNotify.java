package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.battle.event.EvtAvatarStandUpNotify;

public class PacketEvtAvatarStandUpNotify extends BaseTypedPacket<EvtAvatarStandUpNotify> {

    public PacketEvtAvatarStandUpNotify(EvtAvatarStandUpNotify notify) {
        super(new EvtAvatarStandUpNotify(notify.getDirection(), notify.getEntityId(), notify.getPerformId(), notify.getChairId()));
    }
}
