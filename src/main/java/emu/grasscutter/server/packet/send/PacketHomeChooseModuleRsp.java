package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.module.HomeChooseModuleRsp;

public class PacketHomeChooseModuleRsp extends BaseTypedPacket<HomeChooseModuleRsp> {
    public PacketHomeChooseModuleRsp(int moduleId) {
        super(new HomeChooseModuleRsp());
        proto.setRetcode(Retcode.RET_SUCC);
        proto.setModuleId(moduleId);
    }
}
