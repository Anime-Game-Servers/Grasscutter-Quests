package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketMcoinExchangeHcoinRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.item.exchange.McoinExchangeHcoinReq;

public class HandlerMcoinExchangeHcoinReq extends TypedPacketHandler<McoinExchangeHcoinReq> {
    @Override
    public void handle(GameSession session, byte[] header, McoinExchangeHcoinReq req) throws Exception {
        if (session.getPlayer().getCrystals() < req.getMcoinCost() && req.getMcoinCost() == req.getHcoin()) {
            session.send(new PacketMcoinExchangeHcoinRsp(Retcode.RET_UNKNOWN_ERROR));
            return;
        }

        session.getPlayer().setCrystals(session.getPlayer().getCrystals() - req.getMcoinCost());
        session.getPlayer().setPrimogems(session.getPlayer().getPrimogems() + req.getHcoin());
        session.getPlayer().save();
        session.send(new PacketMcoinExchangeHcoinRsp(Retcode.RET_SUCC));
    }
}
