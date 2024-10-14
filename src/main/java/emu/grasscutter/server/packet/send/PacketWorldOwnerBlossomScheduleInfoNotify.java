package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.blossom.BlossomScheduleInfo;
import org.anime_game_servers.multi_proto.gi.messages.blossom.WorldOwnerBlossomScheduleInfoNotify;

public class PacketWorldOwnerBlossomScheduleInfoNotify extends BaseTypedPacket<WorldOwnerBlossomScheduleInfoNotify> {
    public PacketWorldOwnerBlossomScheduleInfoNotify(BlossomScheduleInfo scheduleInfo) {
        super(new WorldOwnerBlossomScheduleInfoNotify());
        proto.setScheduleInfo(scheduleInfo);
    }
}
