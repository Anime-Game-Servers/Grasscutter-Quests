package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.storage.StoreType;
import org.anime_game_servers.multi_proto.gi.messages.item.StoreItemDelNotify;

import java.util.List;

public class PacketStoreItemDelNotify extends BaseTypedPacket<StoreItemDelNotify> {
	public PacketStoreItemDelNotify(GameItem item) {
        this(List.of(item));
	}

    public PacketStoreItemDelNotify(List<GameItem> items) {
        super(new StoreItemDelNotify());
        proto.setStoreType(StoreType.STORE_PACK);
        proto.setGuidList(items.stream().map(GameItem::getGuid).toList());
	}
}
