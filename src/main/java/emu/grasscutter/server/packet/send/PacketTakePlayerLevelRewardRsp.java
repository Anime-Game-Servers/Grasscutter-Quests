package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.player.TakePlayerLevelRewardRsp;

public class PacketTakePlayerLevelRewardRsp extends BaseTypedPacket<TakePlayerLevelRewardRsp> {
    public PacketTakePlayerLevelRewardRsp(int level, int rewardId) {
        super(new TakePlayerLevelRewardRsp());
        proto.setLevel(level);
        proto.setRewardId(rewardId);
        proto.setRetcode(rewardId == 0 ? Retcode.RET_FAIL : Retcode.RET_SUCC);
	}
}
