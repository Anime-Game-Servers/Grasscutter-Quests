package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketHomeEnterEditModeFinishRsp;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.edit.HomeEnterEditModeFinishReq;

public class HandlerHomeEnterEditModeFinishReq extends TypedPacketHandler<HomeEnterEditModeFinishReq> {

    @Override
    public void handle(GameSession session, byte[] header, HomeEnterEditModeFinishReq req) throws Exception {
        /*
         * This packet is about the edit mode
         */
        session.send(new PacketHomeEnterEditModeFinishRsp());
    }
}
