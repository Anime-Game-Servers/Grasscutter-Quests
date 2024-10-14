package emu.grasscutter.server.packet.send;


import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.shop.micro_transaction.CardProductRewardNotify;

public class PacketCardProductRewardNotify extends BaseTypedPacket<CardProductRewardNotify> {
    public PacketCardProductRewardNotify(int remainsDay) {
        super(new CardProductRewardNotify());
        proto.setProductId("ys_chn_blessofmoon_tier5");
        proto.setHcoin(90);
        proto.setRemainDays(remainsDay);

        // Hard code Product id keep cool 😎
    }
}
