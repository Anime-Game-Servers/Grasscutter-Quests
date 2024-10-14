package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.expedition.ExpeditionInfo;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.expedition.AvatarExpeditionCallBackRsp;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.expedition.AvatarExpeditionInfo;

import java.util.HashMap;
import java.util.Map;


public class PacketAvatarExpeditionCallBackRsp extends BaseTypedPacket<AvatarExpeditionCallBackRsp> {
    public PacketAvatarExpeditionCallBackRsp(Map<Long, ExpeditionInfo> expeditionInfo) {
        super(new AvatarExpeditionCallBackRsp());
        Map<Long, AvatarExpeditionInfo> avatarExpeditionInfoMap = new HashMap<>();
        expeditionInfo.forEach((key, e) -> avatarExpeditionInfoMap.put(key, e.toProto()));
        proto.setExpeditionInfoMap(avatarExpeditionInfoMap);
    }
}
