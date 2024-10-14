package emu.grasscutter.game.mail;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.Transient;
import emu.grasscutter.database.DatabaseHelper;
import emu.grasscutter.game.player.Player;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.item.EquipParam;
import org.anime_game_servers.multi_proto.gi.messages.mail.MailCollectState;
import org.anime_game_servers.multi_proto.gi.messages.mail.MailData;
import org.anime_game_servers.multi_proto.gi.messages.mail.MailItem;
import org.anime_game_servers.multi_proto.gi.messages.mail.MailTextContent;
import org.bson.types.ObjectId;

@Entity(value = "mail", useDiscriminator = false)
public class Mail {
	@Getter @Id private ObjectId id;
    @Getter @Setter @Indexed private int ownerUid;
    @Getter public MailContent mailContent;
    @Getter public List<MailItem> itemList;
    @Getter public long sendTime;
    @Getter public long expireTime;
    @Getter public int importance;
    @Getter public boolean isRead;
    @Getter public boolean isAttachmentGot;
    @Getter public int stateValue;
    @Transient private boolean shouldDelete;

    public Mail() {
        this(new MailContent(), new ArrayList<MailItem>(), (int) Instant.now().getEpochSecond() + 604800); // TODO: add expire time to send mail command
    }

    public Mail(MailContent mailContent, List<MailItem> itemList, long expireTime) {
        this(mailContent, itemList, expireTime, 0);
    }

    public Mail(MailContent mailContent, List<MailItem> itemList, long expireTime, int importance) {
        this(mailContent, itemList, expireTime, importance, 1);
    }

    public Mail(MailContent mailContent, List<MailItem> itemList, long expireTime, int importance, int state) {
        this.mailContent = mailContent;
        this.itemList = itemList;
        this.sendTime = (int) Instant.now().getEpochSecond();
        this.expireTime = expireTime;
        this.importance = importance; // Starred mail, 0 = No star, 1 = Star.
        this.isRead = false;
        this.isAttachmentGot = false;
        this.stateValue = state; // Different mailboxes, 1 = Default, 3 = Gift-box.
    }

    public MailData toProto(Player player) {
        val proto = new MailData();
        proto.setMailId(player.getMailId(this));
        proto.setMailTextContent(this.mailContent.toProto());
        proto.setItemList(this.itemList.stream().map(MailItem::toProto).toList());
        proto.setSendTime((int) this.sendTime);
        proto.setExpireTime((int) this.expireTime);
        proto.setImportance(this.importance);
        proto.setRead(this.isRead);
        proto.setAttachmentGot(this.isAttachmentGot);
        proto.setCollectState(MailCollectState.MAIL_NOT_COLLECTIBLE);
        return proto;
    }

	@Entity
    public static class MailContent {
        public String title;
        public String content;
        public String sender;

        public MailContent() {
            this.title = "";
            this.content = "loading...";
            this.sender = "loading";
        }

        public MailContent(String title, String content) {
            this(title, content, "Server");
        }

        public MailContent(String title, String content, Player sender) {
            this(title, content, sender.getNickname());
        }

        public MailContent(String title, String content, String sender) {
            this.title = title;
            this.content = content;
            this.sender = sender;
        }

        public MailTextContent toProto() {
            return new MailTextContent(this.title, this.content, this.sender);
        }
    }

    @Entity
    public static class MailItem {
        public int itemId;
        public int itemCount;
        public int itemLevel;

        public MailItem() {
            this.itemId = 11101;
            this.itemCount = 1;
            this.itemLevel = 1;
        }

        public MailItem(int itemId) {
            this(itemId, 1);
        }

        public MailItem(int itemId, int itemCount) { this(itemId, itemCount, 1); }

        public MailItem(int itemId, int itemCount, int itemLevel) {
            this.itemId = itemId;
            this.itemCount = itemCount;
            this.itemLevel = itemLevel;
        }

        public org.anime_game_servers.multi_proto.gi.messages.mail.MailItem toProto() {
            return new org.anime_game_servers.multi_proto.gi.messages.mail.MailItem(
                null,
                new EquipParam(this.itemId, this.itemCount, this.itemLevel,
                    0 //mock promote level
                    )
            );
        }
    }

	public void save() {
		if (this.expireTime * 1000 < System.currentTimeMillis()) {
			DatabaseHelper.deleteMail(this);
		} else {
			DatabaseHelper.saveMail(this);
		}
	}
}
