package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.props.ActionReason;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.ItemAddHintNotify;

import java.util.Collection;
import java.util.List;

public class PacketItemAddHintNotify extends BaseTypedPacket<ItemAddHintNotify> {

	public PacketItemAddHintNotify(GameItem item, ActionReason reason) {
        super(new ItemAddHintNotify());
        proto.setItemList(List.of(item.toItemHintProto()));
        proto.setReason(reason.getValue());
	}

	public PacketItemAddHintNotify(Collection<GameItem> items, ActionReason reason) {
        super(new ItemAddHintNotify());
        proto.setItemList(items.stream().map(GameItem::toItemHintProto).toList());
        proto.setReason(reason.getValue());
	}
}
