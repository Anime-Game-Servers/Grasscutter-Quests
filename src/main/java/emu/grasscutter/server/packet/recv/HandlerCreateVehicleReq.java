package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;

import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketCreateVehicleRsp;

import emu.grasscutter.utils.Position;
import org.anime_game_servers.multi_proto.gi.messages.gadget.CreateVehicleReq;

public class HandlerCreateVehicleReq extends TypedPacketHandler<CreateVehicleReq> {

	@Override
	public void handle(GameSession session, byte[] header, CreateVehicleReq req) throws Exception {
		session.send(new PacketCreateVehicleRsp(session.getPlayer(), req.getVehicleId(), req.getScenePointId(), new Position(req.getPos()), new Position(req.getRot())));
	}
}
