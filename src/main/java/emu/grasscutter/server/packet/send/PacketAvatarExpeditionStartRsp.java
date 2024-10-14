package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.expedition.ExpeditionInfo;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.expedition.AvatarExpeditionStartRsp;

import java.util.Map;
import java.util.stream.Collectors;

public class PacketAvatarExpeditionStartRsp extends BaseTypedPacket<AvatarExpeditionStartRsp> {
    public PacketAvatarExpeditionStartRsp(Map<Long, ExpeditionInfo> expeditionInfo) {
        super(new AvatarExpeditionStartRsp());
        proto.setExpeditionInfoMap(expeditionInfo.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().toProto()
            )));
    }
}
