package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.gadget.ProjectorOpType;
import org.anime_game_servers.multi_proto.gi.messages.gadget.ProjectorOptionRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketProjectorOptionRsp extends BaseTypedPacket<ProjectorOptionRsp> {

    public PacketProjectorOptionRsp(int entityId, ProjectorOpType opType, Retcode retcode) {
        super(new ProjectorOptionRsp(retcode, entityId, opType));
    }
}
