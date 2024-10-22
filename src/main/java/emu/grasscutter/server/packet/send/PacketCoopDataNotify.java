package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import messages.coop.CoopDataNotify;
import java.util.Map;

public class PacketCoopDataNotify extends BaseTypedPacket<CoopDataNotify> {

    public PacketCoopDataNotify(Player player) {
        super(new CoopDataNotify());
        //chapter list
        proto.setChapterList(player.getCoopHandler().getFullCoopDataList());

        //curCoopPoint, haveProgress
        if (player.getCoopHandler().getCurCoopPoint() > 0) {
            proto.setCurCoopPoint(player.getCoopHandler().getCurCoopPoint());
            proto.setHaveProgress(true);
        }

        //viewed list
        proto.setViewedChapterList(player.getCoopHandler().getCoopCards()
                .entrySet().stream()
                .filter(x -> x.getValue().getViewed())
                .map(Map.Entry::getKey)
                .toList());

    }
}