package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.challenge.ChallengeDataNotify;

public class PacketChallengeDataNotify extends BaseTypedPacket<ChallengeDataNotify> {
    public PacketChallengeDataNotify(WorldChallenge challenge, int index, int value) {
        super(new ChallengeDataNotify());
        proto.setChallengeIndex(challenge.getInfo().getChallengeIndex());
        proto.setParamIndex(index);
        proto.setValue(value);
    }
}
