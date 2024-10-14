package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketBuyResinRsp;
import org.anime_game_servers.multi_proto.gi.messages.item.exchange.BuyResinReq;

public class HandlerBuyResinReq extends TypedPacketHandler<BuyResinReq> {
    @Override
    public void handle(GameSession session, byte[] header, BuyResinReq req) throws Exception {
        var player = session.getPlayer();
        session.send(new PacketBuyResinRsp(player, player.getResinManager().buy()));
    }
}
