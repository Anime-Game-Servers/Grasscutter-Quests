package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.trial.ReceivedTrialAvatarActivityRewardRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketReceivedTrialAvatarActivityRewardRsp extends BaseTypedPacket<ReceivedTrialAvatarActivityRewardRsp> {
	public PacketReceivedTrialAvatarActivityRewardRsp(int activityId, int trialAvatarId, boolean success) {
		this(activityId, trialAvatarId, success ? Retcode.RET_SUCC : Retcode.RET_FAIL);
	}

	public PacketReceivedTrialAvatarActivityRewardRsp(int activityId, int trialAvatarId, Retcode retcode) {
        super(new ReceivedTrialAvatarActivityRewardRsp());
        proto.setActivityId(activityId);
        proto.setTrialAvatarIndexId(trialAvatarId);
        proto.setRetcode(retcode);
	}
}
