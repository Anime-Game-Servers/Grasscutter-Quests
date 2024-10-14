package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.challenge.DungeonChallengeBeginNotify;

public class PacketDungeonChallengeBeginNotify extends BaseTypedPacket<DungeonChallengeBeginNotify> {
    public PacketDungeonChallengeBeginNotify(WorldChallenge challenge) {
        super(new DungeonChallengeBeginNotify(), true);
        proto.setFatherIndex(challenge.getInfo().getFatherChallengeIndex());
        proto.setChallengeId(challenge.getInfo().getChallengeId());
        proto.setChallengeIndex(challenge.getInfo().getChallengeIndex());
        proto.setGroupId(challenge.getGroupId());
        proto.setParamList(challenge.getParamList());
    }
}
