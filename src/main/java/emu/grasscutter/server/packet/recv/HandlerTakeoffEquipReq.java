package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketTakeoffEquipRsp;
import org.anime_game_servers.multi_proto.gi.messages.item.avatar.TakeoffEquipReq;

public class HandlerTakeoffEquipReq extends TypedPacketHandler<TakeoffEquipReq> {
	@Override
    public void handle(GameSession session, byte[] header, TakeoffEquipReq req) throws Exception {
		if (session.getPlayer().getInventory().unequipItem(req.getAvatarGuid(), req.getSlot())) {
			session.send(new PacketTakeoffEquipRsp(req.getAvatarGuid(), req.getSlot()));
		}
	}
}
