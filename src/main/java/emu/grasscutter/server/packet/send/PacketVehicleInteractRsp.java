package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.entity.EntityVehicle;
import emu.grasscutter.game.player.Player;

import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.gadget.VehicleInteractRsp;
import org.anime_game_servers.multi_proto.gi.messages.gadget.VehicleInteractType;
import org.anime_game_servers.multi_proto.gi.messages.general.vehicle.VehicleMember;

public class PacketVehicleInteractRsp extends BaseTypedPacket<VehicleInteractRsp> {

	public PacketVehicleInteractRsp(Player player, int entityId, VehicleInteractType interactType) {
		super(new VehicleInteractRsp());

		val vehicle = player.getScene().getEntityById(entityId);

		if(vehicle instanceof EntityVehicle) {
			proto.setEntityId(vehicle.getId());

			val vehicleMember = new VehicleMember(player.getUid(), player.getTeamManager().getCurrentCharacterGuid());

			proto.setInteractType(interactType);
			proto.setMember(vehicleMember);

			switch(interactType){
                case VEHICLE_INTERACT_IN -> {
					((EntityVehicle) vehicle).getVehicleMembers().add(vehicleMember);
                    player.getQuestManager().queueEvent(QuestContent.QUEST_CONTENT_ENTER_VEHICLE, ((EntityVehicle) vehicle).getGadgetId());
				}
                case VEHICLE_INTERACT_OUT -> {
					((EntityVehicle) vehicle).getVehicleMembers().remove(vehicleMember);
				}
				default -> {}
			}
		}
	}

	public PacketVehicleInteractRsp(EntityVehicle vehicle, VehicleMember vehicleMember, VehicleInteractType interactType) {
		super(new VehicleInteractRsp());

		if(vehicle != null) {
			proto.setEntityId(vehicle.getId());
			proto.setInteractType(interactType);
			proto.setMember(vehicleMember);

			switch(interactType){
				case VEHICLE_INTERACT_IN -> {
					vehicle.getVehicleMembers().add(vehicleMember);
				}
				case VEHICLE_INTERACT_OUT -> {
					vehicle.getVehicleMembers().remove(vehicleMember);
				}
				default -> {}
			}
		}
	}
}
