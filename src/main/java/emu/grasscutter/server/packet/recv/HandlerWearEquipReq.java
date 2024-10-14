package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketWearEquipRsp;
import org.anime_game_servers.multi_proto.gi.messages.item.avatar.WearEquipReq;

public class HandlerWearEquipReq extends TypedPacketHandler<WearEquipReq> {
	@Override
    public void handle(GameSession session, byte[] header, WearEquipReq req) throws Exception {
		if (session.getPlayer().getInventory().equipItem(req.getAvatarGuid(), req.getEquipGuid())) {
			session.send(new PacketWearEquipRsp(req.getAvatarGuid(), req.getEquipGuid()));
		}
	}
}
