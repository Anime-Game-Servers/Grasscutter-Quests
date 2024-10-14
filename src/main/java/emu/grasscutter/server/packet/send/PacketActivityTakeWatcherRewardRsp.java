package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.ActivityTakeWatcherRewardRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketActivityTakeWatcherRewardRsp extends BaseTypedPacket<ActivityTakeWatcherRewardRsp> {

	public PacketActivityTakeWatcherRewardRsp(int activityId, int watcherId) {
		super(new ActivityTakeWatcherRewardRsp(Retcode.RET_SUCC, activityId, watcherId));
	}

}
