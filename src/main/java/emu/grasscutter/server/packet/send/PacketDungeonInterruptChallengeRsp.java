package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.challenge.DungeonInterruptChallengeRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketDungeonInterruptChallengeRsp extends BaseTypedPacket<DungeonInterruptChallengeRsp> {
    public PacketDungeonInterruptChallengeRsp(boolean result, int challengeId, int challengeIndex, int groupId) {
        super(new DungeonInterruptChallengeRsp());
        proto.setChallengeId(challengeId);
        proto.setChallengeIndex(challengeIndex);
        proto.setGroupId(groupId);
        proto.setRetcode(result ? Retcode.RET_SUCC : Retcode.RET_FAIL);
    }
}
