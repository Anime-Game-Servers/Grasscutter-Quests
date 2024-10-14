package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.home.FurnitureMakeSlotItem;
import emu.grasscutter.game.home.GameHome;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.furniture.FurnitureMakeRsp;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.furniture.FurnitureMakeSlot;

public class PacketFurnitureMakeRsp extends BaseTypedPacket<FurnitureMakeRsp> {

	public PacketFurnitureMakeRsp(GameHome home) {
        super(new FurnitureMakeRsp());
        val furnitureMakeSlot = new FurnitureMakeSlot();
        furnitureMakeSlot.setFurnitureMakeDataList(home.getFurnitureMakeSlotItemList().stream()
            .map(FurnitureMakeSlotItem::toProto)
            .toList());
        proto.setFurnitureMakeSlot(furnitureMakeSlot);
	}
}
