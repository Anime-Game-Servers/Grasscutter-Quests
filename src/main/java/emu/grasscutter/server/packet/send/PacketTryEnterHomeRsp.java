package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.player.TryEnterHomeRsp;

public class PacketTryEnterHomeRsp extends BaseTypedPacket<TryEnterHomeRsp> {

    public PacketTryEnterHomeRsp() {
        super(new TryEnterHomeRsp());
        proto.setRetcode(Retcode.RET_SVR_ERROR);
    }

    public PacketTryEnterHomeRsp(int uid) {
        this(Retcode.RET_SUCC, uid);
    }

    public PacketTryEnterHomeRsp(Retcode retCode, int uid) {
        super(new TryEnterHomeRsp());
        proto.setRetcode(retCode);
        proto.setTargetUid(uid);
    }
}
