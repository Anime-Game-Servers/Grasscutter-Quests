package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetPlayerAskFriendListRsp;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.GetPlayerAskFriendListReq;

public class HandlerGetPlayerAskFriendListReq extends TypedPacketHandler<GetPlayerAskFriendListReq> {
    @Override
    public void handle(GameSession session, byte[] header, GetPlayerAskFriendListReq req) throws Exception {
        session.send(new PacketGetPlayerAskFriendListRsp(session.getPlayer()));
    }
}
