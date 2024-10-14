package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.challenge.ChallengeFinishType;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.challenge.DungeonChallengeFinishNotify;

public class PacketDungeonChallengeFinishNotify extends BaseTypedPacket<DungeonChallengeFinishNotify> {
    public PacketDungeonChallengeFinishNotify(WorldChallenge challenge) {
        super(new DungeonChallengeFinishNotify(), true);

        proto.setChallengeIndex(challenge.getInfo().getChallengeIndex());
        proto.setSuccess(challenge.isSuccess());
        proto.setTimeCost(challenge.getFinishedTime() - challenge.getStartedAt());
        proto.setFinishType(challenge.isSuccess() ?
                ChallengeFinishType.CHALLENGE_FINISH_TYPE_SUCC :
                ChallengeFinishType.CHALLENGE_FINISH_TYPE_FAIL
        );
    }
}
