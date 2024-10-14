package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.battle_pass.BuyBattlePassLevelRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketBuyBattlePassLevelRsp extends BaseTypedPacket<BuyBattlePassLevelRsp> {

	public PacketBuyBattlePassLevelRsp(int buyLevel) {
		super(new BuyBattlePassLevelRsp(Retcode.RET_SUCC, buyLevel));
	}
}
