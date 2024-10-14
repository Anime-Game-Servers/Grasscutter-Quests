package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSetChatEmojiCollectionRsp;
import org.anime_game_servers.multi_proto.gi.messages.community.chat.SetChatEmojiCollectionReq;

public class HandlerSetChatEmojiCollectionReq extends TypedPacketHandler<SetChatEmojiCollectionReq> {
    @Override
    public void handle(GameSession session, byte[] header, SetChatEmojiCollectionReq req) throws Exception {
        session.getPlayer().setChatEmojiIdList(req.getChatEmojiCollectionData().getEmojiIdList());
        session.send(new PacketSetChatEmojiCollectionRsp());
    }
}
