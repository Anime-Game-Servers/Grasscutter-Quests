package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.expedition.ExpeditionInfo;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.expedition.AvatarExpeditionGetRewardRsp;

import java.util.*;
import java.util.stream.Collectors;

public class PacketAvatarExpeditionGetRewardRsp extends BaseTypedPacket<AvatarExpeditionGetRewardRsp> {
    public PacketAvatarExpeditionGetRewardRsp(Map<Long, ExpeditionInfo> expeditionInfo, Collection<GameItem> items) {
        super(new AvatarExpeditionGetRewardRsp());
        proto.setExpeditionInfoMap(expeditionInfo.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().toProto()
            )));
        proto.setItemList(items.stream().map(GameItem::toItemParam).toList());
    }
}
