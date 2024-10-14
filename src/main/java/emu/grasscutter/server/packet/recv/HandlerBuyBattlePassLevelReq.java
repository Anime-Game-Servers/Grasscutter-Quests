package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketBuyBattlePassLevelRsp;
import org.anime_game_servers.multi_proto.gi.messages.battle_pass.BuyBattlePassLevelReq;

public class HandlerBuyBattlePassLevelReq extends TypedPacketHandler<BuyBattlePassLevelReq> {

	@Override
	public void handle(GameSession session, byte[] header, BuyBattlePassLevelReq req) throws Exception {
		int buyLevel = session.getPlayer().getBattlePassManager().buyLevels(req.getBuyLevel());

		session.send(new PacketBuyBattlePassLevelRsp(buyLevel));
	}

}
