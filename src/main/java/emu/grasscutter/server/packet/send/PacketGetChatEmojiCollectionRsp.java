package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.community.chat.ChatEmojiCollectionData;
import org.anime_game_servers.multi_proto.gi.messages.community.chat.GetChatEmojiCollectionRsp;

import java.util.List;

public class PacketGetChatEmojiCollectionRsp extends BaseTypedPacket<GetChatEmojiCollectionRsp> {
    public PacketGetChatEmojiCollectionRsp(List<Integer> emojiIds) {
        super(new GetChatEmojiCollectionRsp());
        proto.setChatEmojiCollectionData(new ChatEmojiCollectionData(emojiIds));
    }
}
