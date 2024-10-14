package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketVehicleInteractRsp;
import org.anime_game_servers.multi_proto.gi.messages.gadget.VehicleInteractReq;

public class HandlerVehicleInteractReq extends TypedPacketHandler<VehicleInteractReq> {
	@Override
	public void handle(GameSession session, byte[] header, VehicleInteractReq req) throws Exception {
		session.getPlayer().getStaminaManager().handleVehicleInteractReq(session, req.getEntityId(), req.getInteractType());
		session.send(new PacketVehicleInteractRsp(session.getPlayer(), req.getEntityId(), req.getInteractType()));
	}
}
