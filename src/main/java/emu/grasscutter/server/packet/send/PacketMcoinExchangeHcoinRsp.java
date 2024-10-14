package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.item.exchange.McoinExchangeHcoinRsp;

public class PacketMcoinExchangeHcoinRsp extends BaseTypedPacket<McoinExchangeHcoinRsp> {
    public PacketMcoinExchangeHcoinRsp(Retcode retCode) {
        super(new McoinExchangeHcoinRsp());
        proto.setRetCode(retCode);
    }
}
