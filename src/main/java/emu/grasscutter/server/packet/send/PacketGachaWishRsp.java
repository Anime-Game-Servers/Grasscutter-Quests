package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.wishing.GachaWishRsp;

public class PacketGachaWishRsp extends BaseTypedPacket<GachaWishRsp> {
    public PacketGachaWishRsp(int gachaType, int scheduleId, int itemId, int progress, int maxProgress) {
        super(new GachaWishRsp());
        proto.setGachaType(gachaType);
        proto.setGachaScheduleId(scheduleId);
        proto.setWishItemId(itemId);
        proto.setWishProgress(progress);
        proto.setWishMaxProgress(maxProgress);
    }
}
