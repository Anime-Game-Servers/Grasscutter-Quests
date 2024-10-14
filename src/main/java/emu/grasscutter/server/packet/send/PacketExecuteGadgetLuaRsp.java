
package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.scene.script.ExecuteGadgetLuaRsp;

public class PacketExecuteGadgetLuaRsp extends BaseTypedPacket<ExecuteGadgetLuaRsp> {
    public PacketExecuteGadgetLuaRsp(Retcode result) {
        super(new ExecuteGadgetLuaRsp(), true);
        proto.setRetcode(result);
    }
}
