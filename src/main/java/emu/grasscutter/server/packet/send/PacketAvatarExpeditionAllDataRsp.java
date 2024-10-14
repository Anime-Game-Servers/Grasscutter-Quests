package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.expedition.ExpeditionInfo;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.expedition.AvatarExpeditionAllDataRsp;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PacketAvatarExpeditionAllDataRsp extends BaseTypedPacket<AvatarExpeditionAllDataRsp> {
    public PacketAvatarExpeditionAllDataRsp(Map<Long, ExpeditionInfo> expeditionInfo, int expeditionCountLimit) {
        super(new AvatarExpeditionAllDataRsp());
        //TODO: don't hardcode values. These look like the ID in ExpeditionDataExcelConfigData
        var openExpeditionList  = List.of(306,305,304,303,302,301,206,105,204,104,203,103,202,101,102,201,106,205,401,402,403,404,405,406);


        proto.setOpenExpeditionList(openExpeditionList);
        proto.setExpeditionCountLimit(expeditionCountLimit);
        proto.setExpeditionInfoMap(
                expeditionInfo.entrySet().stream()
                    .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue().toProto())));

    }
}
