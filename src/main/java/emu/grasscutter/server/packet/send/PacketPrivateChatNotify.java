package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.community.chat.ChatInfo;
import org.anime_game_servers.multi_proto.gi.messages.community.chat.PrivateChatNotify;

public class PacketPrivateChatNotify extends BaseTypedPacket<PrivateChatNotify> {
    private ChatInfo info;

    public PacketPrivateChatNotify(int senderId, int recvId, String message) {
        this(senderId, recvId, new ChatInfo.Content.Text(message));
    }

    public PacketPrivateChatNotify(int senderId, int recvId, int emote) {
        this(senderId, recvId, new ChatInfo.Content.Icon(emote));
    }
    private PacketPrivateChatNotify(int senderId, int recvId, ChatInfo.Content<?> content) {
        super(new PrivateChatNotify());

        val info = new ChatInfo();
        info.setTime((int) (System.currentTimeMillis() / 1000));
        info.setUid(senderId);
        info.setToUid(recvId);
        info.setContent(content);
        this.info = info;

        proto.setChatInfo(info);
    }

    public ChatInfo getChatInfo() {
        return this.info;
    }
}
