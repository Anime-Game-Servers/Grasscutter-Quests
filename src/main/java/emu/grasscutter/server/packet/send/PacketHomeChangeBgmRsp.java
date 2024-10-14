package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.bgm.HomeChangeBgmRsp;

public class PacketHomeChangeBgmRsp extends BaseTypedPacket<HomeChangeBgmRsp> {
    public PacketHomeChangeBgmRsp() {
        super(new HomeChangeBgmRsp());
        proto.setRetcode(Retcode.RET_SUCC);
    }
}
