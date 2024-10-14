package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.trial.EnterTrialAvatarActivityDungeonRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketEnterTrialAvatarActivityDungeonRsp extends BaseTypedPacket<EnterTrialAvatarActivityDungeonRsp> {
	public PacketEnterTrialAvatarActivityDungeonRsp(int activityId, int trialAvatarIndexId, boolean success) {
		this(activityId, trialAvatarIndexId, success ? Retcode.RET_SUCC : Retcode.RET_FAIL);
	}

	public PacketEnterTrialAvatarActivityDungeonRsp(int activityId, int trialAvatarIndexId, Retcode retcodeVal) {
        super(new EnterTrialAvatarActivityDungeonRsp());
        proto.setActivityId(activityId);
        proto.setTrialAvatarIndexId(trialAvatarIndexId);
        proto.setRetcode(retcodeVal);
	}
}
