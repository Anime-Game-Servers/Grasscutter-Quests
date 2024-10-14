package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetInvestigationMonsterRsp;
import org.anime_game_servers.multi_proto.gi.messages.world.investigation.GetInvestigationMonsterReq;

public class HandlerGetInvestigationMonsterReq extends TypedPacketHandler<GetInvestigationMonsterReq> {
    @Override
    public void handle(GameSession session, byte[] header, GetInvestigationMonsterReq req) throws Exception {
        session.send(new PacketGetInvestigationMonsterRsp(
                session.getPlayer(),
                session.getServer().getWorldDataSystem(),
            req.getCityIdList()));
    }
}
