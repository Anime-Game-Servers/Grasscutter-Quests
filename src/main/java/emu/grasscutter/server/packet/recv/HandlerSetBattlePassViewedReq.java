package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSetBattlePassViewedRsp;
import org.anime_game_servers.multi_proto.gi.messages.battle_pass.SetBattlePassViewedReq;

public class HandlerSetBattlePassViewedReq extends TypedPacketHandler<SetBattlePassViewedReq> {
    @Override
    public void handle(GameSession session, byte[] header, SetBattlePassViewedReq req) throws Exception {
        session.getPlayer().getBattlePassManager().updateViewed();
        session.send(new PacketSetBattlePassViewedRsp(req.getScheduleId()));
    }
}
