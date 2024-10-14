package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.edit.HomePreChangeEditModeNotify;

public class PacketHomePreChangeEditModeNotify extends BaseTypedPacket<HomePreChangeEditModeNotify> {
    public PacketHomePreChangeEditModeNotify(boolean isEnterEditMode) {
        super(new HomePreChangeEditModeNotify());
        proto.setEnterEditMode(isEnterEditMode);
    }
}
