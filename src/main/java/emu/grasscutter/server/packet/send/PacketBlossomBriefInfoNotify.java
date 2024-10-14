package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.blossom.BlossomBriefInfo;
import org.anime_game_servers.multi_proto.gi.messages.blossom.BlossomBriefInfoNotify;

import java.util.List;

public class PacketBlossomBriefInfoNotify extends BaseTypedPacket<BlossomBriefInfoNotify> {
    public PacketBlossomBriefInfoNotify(List<BlossomBriefInfo> blossoms) {
        super(new BlossomBriefInfoNotify());
        proto.setBriefInfoList(blossoms);
    }
}
