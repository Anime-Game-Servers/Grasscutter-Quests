package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.gadget.VehicleStaminaNotify;

public class PacketVehicleStaminaNotify extends BaseTypedPacket<VehicleStaminaNotify> {
	public PacketVehicleStaminaNotify(int vehicleId, float newStamina) {
        super(new VehicleStaminaNotify());
		proto.setEntityId(vehicleId);
		proto.setCurStamina(newStamina);
	}
}
