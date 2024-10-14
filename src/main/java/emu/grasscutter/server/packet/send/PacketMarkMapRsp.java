package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.managers.mapmark.MapMark;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.scene.map.MapMarkPoint;
import org.anime_game_servers.multi_proto.gi.messages.scene.map.MarkMapRsp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PacketMarkMapRsp extends BaseTypedPacket<MarkMapRsp> {

    public PacketMarkMapRsp(Map<String, MapMark> mapMarks) {
        super(new MarkMapRsp());
        proto.setRetcode(Retcode.RET_SUCC);

        if (mapMarks != null) {
            List<MapMarkPoint> markPointList = new ArrayList<>();
            for (MapMark mapMark: mapMarks.values()) {
                val markPoint = new MapMarkPoint();
                markPoint.setSceneId(mapMark.getSceneId());
                markPoint.setName(mapMark.getName());
                markPoint.setPos(mapMark.getPosition().toProto());
                markPoint.setPointType(mapMark.getMapMarkPointType());
                markPoint.setFromType(mapMark.getMapMarkFromType());
                markPoint.setMonsterId(mapMark.getMonsterId());
                markPoint.setQuestId(mapMark.getQuestId());
                markPointList.add(markPoint);
            }
            proto.setMarkList(markPointList);
        }
    }
}
