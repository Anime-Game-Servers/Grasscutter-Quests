package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.item.forge.ForgeQueueManipulateRsp;
import org.anime_game_servers.multi_proto.gi.messages.item.forge.ForgeQueueManipulateType;

import java.util.List;

public class PacketForgeQueueManipulateRsp extends BaseTypedPacket<ForgeQueueManipulateRsp> {
    public PacketForgeQueueManipulateRsp(Retcode retcode, ForgeQueueManipulateType type, List<GameItem> output, List<GameItem> refund, List<GameItem> extra) {
        super(new ForgeQueueManipulateRsp());
        proto.setRetCode(retcode);
        proto.setManipulateType(type);
        proto.setOutputItemList(output.stream().map(GameItem::toItemParam).toList());
        proto.setReturnItemList(refund.stream().map(GameItem::toItemParam).toList());
        // ToDo: Add extra items when once we have handling for it.
    }
}
