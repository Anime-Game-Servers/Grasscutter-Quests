package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetChatEmojiCollectionRsp;
import org.anime_game_servers.multi_proto.gi.messages.community.chat.GetChatEmojiCollectionReq;

public class HandlerGetChatEmojiCollectionReq extends TypedPacketHandler<GetChatEmojiCollectionReq> {
    @Override
    public void handle(GameSession session, byte[] header, GetChatEmojiCollectionReq req) throws Exception {
        session.send(new PacketGetChatEmojiCollectionRsp(session.getPlayer().getChatEmojiIdList()));
    }
}
