package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketCheckUgcUpdateRsp;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.CheckUgcUpdateReq;

public class HandlerCheckUgcUpdateReq extends TypedPacketHandler<CheckUgcUpdateReq> {

    @Override
    public void handle(GameSession session, byte[] header, CheckUgcUpdateReq payload) throws Exception {
        session.send(new PacketCheckUgcUpdateRsp(payload.getUgcType()));
    }

}
