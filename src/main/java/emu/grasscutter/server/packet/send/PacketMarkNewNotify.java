package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.map.MarkNewNotify;

import java.util.List;

public class PacketMarkNewNotify extends BaseTypedPacket<MarkNewNotify> {
    public PacketMarkNewNotify(int markNewType, List<Integer> idList) {
        super(new MarkNewNotify());
        proto.setMarkNewType(markNewType);
        proto.setIdList(idList);
    }
}
