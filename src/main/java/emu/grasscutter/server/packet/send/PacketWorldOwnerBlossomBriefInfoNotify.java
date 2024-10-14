package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.world.World;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.blossom.WorldOwnerBlossomBriefInfoNotify;

public class PacketWorldOwnerBlossomBriefInfoNotify extends BaseTypedPacket<WorldOwnerBlossomBriefInfoNotify> {
    public PacketWorldOwnerBlossomBriefInfoNotify(World world) {
        super(new WorldOwnerBlossomBriefInfoNotify());
        proto.setBriefInfoList(world.getOwner().getBlossomManager().getBriefInfo());
    }
}
