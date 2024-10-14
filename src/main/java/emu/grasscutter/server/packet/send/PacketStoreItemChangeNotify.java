package emu.grasscutter.server.packet.send;

import java.util.Collection;
import java.util.List;

import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.storage.StoreItemChangeNotify;
import org.anime_game_servers.multi_proto.gi.messages.storage.StoreType;

public class PacketStoreItemChangeNotify extends BaseTypedPacket<StoreItemChangeNotify> {

	private PacketStoreItemChangeNotify() {
		super(new StoreItemChangeNotify());
	}

	public PacketStoreItemChangeNotify(GameItem item) {
		this();
        proto.setStoreType(StoreType.STORE_PACK);
        proto.setItemList(List.of(item.toProto()));
	}

	public PacketStoreItemChangeNotify(Collection<GameItem> items) {
		this();

        proto.setStoreType(StoreType.STORE_PACK);
        proto.setItemList(items.stream().map(GameItem::toProto).toList());
	}
}
