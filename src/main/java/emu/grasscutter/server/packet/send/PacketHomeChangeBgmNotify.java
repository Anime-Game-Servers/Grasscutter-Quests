package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.bgm.HomeChangeBgmNotify;

public class PacketHomeChangeBgmNotify extends BaseTypedPacket<HomeChangeBgmNotify> {
    public PacketHomeChangeBgmNotify(int homeBgmId) {
        super(new HomeChangeBgmNotify());
        proto.setBgmId(homeBgmId);
    }
}
