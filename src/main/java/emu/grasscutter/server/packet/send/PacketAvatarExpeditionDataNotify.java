package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.expedition.ExpeditionInfo;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.expedition.AvatarExpeditionDataNotify;

import java.util.Map;
import java.util.stream.Collectors;

public class PacketAvatarExpeditionDataNotify extends BaseTypedPacket<AvatarExpeditionDataNotify> {
    public PacketAvatarExpeditionDataNotify(Map<Long, ExpeditionInfo> expeditionInfo) {
        super(new AvatarExpeditionDataNotify());
        proto.setExpeditionInfoMap(
                expeditionInfo.entrySet().stream()
                    .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue().toProto())));
    }
}
