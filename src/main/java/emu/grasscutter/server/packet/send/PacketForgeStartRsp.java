package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.item.forge.ForgeStartRsp;

public class PacketForgeStartRsp extends BaseTypedPacket<ForgeStartRsp> {

    public PacketForgeStartRsp(Retcode retcode) {
        super(new ForgeStartRsp());
        proto.setRetCode(retcode);
    }
}
