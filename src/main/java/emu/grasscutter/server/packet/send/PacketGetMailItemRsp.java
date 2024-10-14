package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.mail.Mail;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.ActionReason;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.item.EquipParam;
import org.anime_game_servers.multi_proto.gi.messages.mail.GetMailItemRsp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PacketGetMailItemRsp  extends BaseTypedPacket<GetMailItemRsp> {

    public PacketGetMailItemRsp(Player player, List<Integer> mailList) {
        super(new GetMailItemRsp());
        List<Mail> claimedMessages = new ArrayList<>();
        List<EquipParam> claimedItems = new ArrayList<>();

        synchronized (player) {
            boolean modified = false;
            for (int mailId : mailList) {
                Mail message = player.getMail(mailId);
                if (!message.isAttachmentGot) {//No duplicated item
                    for (Mail.MailItem mailItem : message.itemList) {
                        val item = new EquipParam();
                        int promoteLevel = GameItem.getMinPromoteLevel(mailItem.itemLevel);

                        item.setItemId(mailItem.itemId);
                        item.setItemNum(mailItem.itemCount);
                        item.setItemLevel(mailItem.itemLevel);
                        item.setPromoteLevel(promoteLevel);
                        claimedItems.add(item);

                        GameItem gameItem = new GameItem(GameData.getItemDataMap().get(mailItem.itemId));
                        gameItem.setCount(mailItem.itemCount);
                        gameItem.setLevel(mailItem.itemLevel);
                        gameItem.setPromoteLevel(promoteLevel);
                        player.getInventory().addItem(gameItem, ActionReason.MailAttachment);
                    }

                    message.isAttachmentGot = true;
                    claimedMessages.add(message);

                    player.replaceMailByIndex(mailId, message);
                    modified = true;
                }
            }
            if(modified) {
                player.save();
            }
        }

        proto.setMailIdList(claimedMessages.stream().map(player::getMailId).collect(Collectors.toList()));
        proto.setItemList(claimedItems);

        player.getSession().send(new PacketMailChangeNotify(player, claimedMessages)); // For some reason you have to also send the MailChangeNotify packet
    }
}
