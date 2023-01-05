package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.RetcodeOuterClass.Retcode;
import emu.grasscutter.net.proto.ReceivedTrialAvatarActivityRewardRspOuterClass.ReceivedTrialAvatarActivityRewardRsp;

public class PacketReceivedTrialAvatarActivityRewardRsp extends BasePacket {
	
	public PacketReceivedTrialAvatarActivityRewardRsp(int activityId, int trialAvatarId, int retcodeVal) {
		super(PacketOpcodes.ReceivedTrialAvatarActivityRewardRsp);
		this.setData(ReceivedTrialAvatarActivityRewardRsp.newBuilder()
			.setActivityId(activityId)
			.setTrialAvatarIndexId(trialAvatarId)
			.setRetcode(retcodeVal)
			.build());
	}
}
