package emu.grasscutter.server.packet.send;


import emu.grasscutter.game.mail.Mail;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.item.EquipParam;
import org.anime_game_servers.multi_proto.gi.messages.mail.*;

import java.util.ArrayList;
import java.util.List;

public class PacketMailChangeNotify extends BaseTypedPacket<MailChangeNotify> {

    public PacketMailChangeNotify(Player player, Mail message) {
        this (player, List.of(message));
    }

    public PacketMailChangeNotify(Player player, List<Mail> mailList) {
        this(player, mailList, null);
    }

    public PacketMailChangeNotify(Player player, List<Mail> mailList, List<Integer> delMailIdList) {
        super(new MailChangeNotify());

        val protoMailList = new ArrayList<MailData>();
        if (mailList != null) {
            for (Mail message : mailList) {
                val mailTextContent = new MailTextContent();
                mailTextContent.setTitle(message.mailContent.title);
                mailTextContent.setContent(message.mailContent.content);
                mailTextContent.setSender(message.mailContent.sender);

                val mailItems = new ArrayList<MailItem>();

                for (Mail.MailItem item : message.itemList) {
                    val mailItem = new MailItem();
                    val itemParam = new EquipParam();
                    itemParam.setItemId(item.itemId);
                    itemParam.setItemNum(item.itemCount);
                    mailItem.setEquipParam(itemParam);

                    mailItems.add(mailItem);
                }

                val mailData = new MailData();
                mailData.setMailId(player.getMailId(message));
                mailData.setMailTextContent(mailTextContent);
                mailData.setItemList(mailItems);
                mailData.setSendTime((int) message.sendTime);
                mailData.setExpireTime((int) message.expireTime);
                mailData.setImportance(message.importance);
                mailData.setRead(message.isRead);
                mailData.setAttachmentGot(message.isAttachmentGot);
                mailData.setCollectState(MailCollectState.values()[message.stateValue]);

                protoMailList.add(mailData);
            }
        }
        proto.setMailList(protoMailList);

        if (delMailIdList != null) {
            proto.setDelMailIdList(delMailIdList);
        }
    }
}
