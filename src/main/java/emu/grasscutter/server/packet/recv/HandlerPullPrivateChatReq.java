package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.community.chat.PullPrivateChatReq;

public class HandlerPullPrivateChatReq extends TypedPacketHandler<PullPrivateChatReq> {

    @Override
    public void handle(GameSession session, byte[] header, PullPrivateChatReq req) throws Exception {
        session.getServer().getChatSystem().handlePullPrivateChatReq(session.getPlayer(), req.getTargetUid());

        // session.send(new PacketPullPrivateChatRsp(req.getTargetUid()));
    }

}
