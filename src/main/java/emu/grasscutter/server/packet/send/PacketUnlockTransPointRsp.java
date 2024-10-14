package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.scene.UnlockTransPointRsp;

public class PacketUnlockTransPointRsp extends BaseTypedPacket<UnlockTransPointRsp> {
    public PacketUnlockTransPointRsp(boolean unlocked) {
        super(new UnlockTransPointRsp());
        proto.setRetcode(unlocked ? Retcode.RET_SUCC : Retcode.RET_FAIL);
    }
}
