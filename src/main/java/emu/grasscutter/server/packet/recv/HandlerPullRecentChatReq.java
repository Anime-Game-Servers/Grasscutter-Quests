package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.community.chat.PullRecentChatReq;

public class HandlerPullRecentChatReq extends TypedPacketHandler<PullRecentChatReq> {
    @Override
    public void handle(GameSession session, byte[] header, PullRecentChatReq req) throws Exception {
        session.getServer().getChatSystem().handlePullRecentChatReq(session.getPlayer());
    }
}
