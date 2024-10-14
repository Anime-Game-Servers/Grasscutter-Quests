package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.mail.DelMailRsp;

import java.util.List;

public class PacketDelMailRsp  extends BaseTypedPacket<DelMailRsp> {

    public PacketDelMailRsp(List<Integer> toDeleteIds) {
        super(new DelMailRsp(Retcode.RET_SUCC, toDeleteIds));
    }
}
