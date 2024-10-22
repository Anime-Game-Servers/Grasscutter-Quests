package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketFinishMainCoopRsp;
import messages.coop.FinishMainCoopReq;

public class HandlerFinishMainCoopReq extends TypedPacketHandler<FinishMainCoopReq> {

    @Override
    public void handle(GameSession session, byte[] header, FinishMainCoopReq req) throws Exception {
        //TODO: anything else here? delete quests?
        session.send(new PacketFinishMainCoopRsp(req.getId(), req.getEndingSavePointId()));
    }

}