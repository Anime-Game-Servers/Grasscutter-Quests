package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketCheckUgcStateRsp;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.CheckUgcStateReq;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class HandlerCheckUgcStateReq extends TypedPacketHandler<CheckUgcStateReq> {

    @Override
    public void handle(GameSession session, byte[] header, CheckUgcStateReq payload) throws Exception {
        session.send(new PacketCheckUgcStateRsp(Retcode.RET_SUCC));
    }

}
