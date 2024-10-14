package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.activity.ActivityManager;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.GetActivityInfoRsp;

import java.util.Set;

public class PacketGetActivityInfoRsp extends BaseTypedPacket<GetActivityInfoRsp> {
	public PacketGetActivityInfoRsp(Set<Integer> activityIdList, ActivityManager activityManager) {
		super(new GetActivityInfoRsp());

        proto.setActivityInfoList(activityIdList.stream()
            .map(activityManager::getInfoProtoByActivityId)
            .toList()
        );
	}
}
