package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.utils.Utils;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.mail.GetAllMailResultNotify;

import java.time.Instant;
import java.util.List;

/**
 * Used since 3.0 for mail requests.
 */
public class PacketGetAllMailResultNotify extends BaseTypedPacket<GetAllMailResultNotify> {

    public PacketGetAllMailResultNotify(Player player, boolean isGiftMail) {
        super(new GetAllMailResultNotify());

        proto.setTransaction(player.getUid() + "-" + Utils.getCurrentSeconds() + "-" + 0);
        proto.setCollected(isGiftMail);
        proto.setPageIndex(1);
        proto.setTotalPageCount(1);

        val inbox = player.getAllMail();
        if (!isGiftMail && inbox.size() > 0) {
            proto.setMailList(inbox.stream()
                .filter(mail -> mail.stateValue == 1)
                .filter(mail -> mail.expireTime > Instant.now().getEpochSecond())
                .map(mail -> mail.toProto(player)).toList());
        } else {
            // Empty mailbox.
            // TODO: Implement the gift mailbox.
            proto.setMailList(List.of());
        }
    }
}
