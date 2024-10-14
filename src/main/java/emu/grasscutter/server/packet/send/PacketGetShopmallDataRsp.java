package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.shop.GetShopmallDataRsp;

import java.util.List;

public class PacketGetShopmallDataRsp extends BaseTypedPacket<GetShopmallDataRsp> {

	public PacketGetShopmallDataRsp() {
		super(new GetShopmallDataRsp());

		val shop_malls = List.of(900, 1052, 902, 1001, 903);

		proto.setShopTypeList(shop_malls);
	}
}
