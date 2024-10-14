package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.community.chat.SetChatEmojiCollectionRsp;

public class PacketSetChatEmojiCollectionRsp extends BaseTypedPacket<SetChatEmojiCollectionRsp> {
    public PacketSetChatEmojiCollectionRsp() {
        super(new SetChatEmojiCollectionRsp());
    }
}
