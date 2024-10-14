package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.mail.GetAllMailRsp;

import java.time.Instant;
import java.util.List;

public class PacketGetAllMailRsp extends BaseTypedPacket<GetAllMailRsp> {

    public PacketGetAllMailRsp(Player player, boolean isGiftMail) {
        super(new GetAllMailRsp());

        proto.setCollected(isGiftMail);

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
        // When enabled this will send a notification to the user telling them their inbox is full, and they should delete old messages when opening the mailbox.
        proto.setTruncated(inbox.size() > 1000);
    }
}
