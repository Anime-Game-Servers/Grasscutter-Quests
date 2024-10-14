package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.furniture.FurnitureMakeData;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.furniture.FurnitureMakeSlot;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.furniture.FurnitureMakeStartRsp;

import java.util.List;

public class PacketFurnitureMakeStartRsp extends BaseTypedPacket<FurnitureMakeStartRsp> {
    public PacketFurnitureMakeStartRsp(Retcode ret, List<FurnitureMakeData> furnitureMakeData) {
        super(new FurnitureMakeStartRsp());
        proto.setRetcode(ret);

        if (furnitureMakeData != null) {
            val furnitureMakeSlot = new FurnitureMakeSlot();
            furnitureMakeSlot.setFurnitureMakeDataList(furnitureMakeData);
            proto.setFurnitureMakeSlot(furnitureMakeSlot);
        }
    }
}
