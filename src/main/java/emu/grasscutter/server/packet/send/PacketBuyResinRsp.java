package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.item.exchange.BuyResinRsp;

public class PacketBuyResinRsp extends BaseTypedPacket<BuyResinRsp> {
	public PacketBuyResinRsp(Player player, Retcode ret) {
        super(new BuyResinRsp());
        proto.setCurValue(player.getProperty(PlayerProperty.PROP_PLAYER_RESIN));
        proto.setRetCode(ret);
    }
}
