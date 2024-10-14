package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.blossom.BlossomChestCreateNotify;

public class PacketBlossomChestCreateNotify extends BaseTypedPacket<BlossomChestCreateNotify> {
    public PacketBlossomChestCreateNotify(int refreshId, int campId) {
        super(new BlossomChestCreateNotify());
        proto.setRefreshId(refreshId);
        proto.setCircleCampId(campId);
    }
}
