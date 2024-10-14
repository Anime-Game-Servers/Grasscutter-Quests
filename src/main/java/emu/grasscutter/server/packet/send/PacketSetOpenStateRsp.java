package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.player.SetOpenStateRsp;

public class PacketSetOpenStateRsp extends BaseTypedPacket<SetOpenStateRsp> {
    public PacketSetOpenStateRsp(int openState, int value) {
        super(new SetOpenStateRsp(Retcode.RET_SUCC, openState, value));
    }

    public PacketSetOpenStateRsp(Retcode retcode) {
        super(new SetOpenStateRsp(retcode));
    }
}
