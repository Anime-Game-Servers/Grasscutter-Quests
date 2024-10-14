package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import emu.grasscutter.net.packet.TypedPacketHandler;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.challenge.DungeonInterruptChallengeReq;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketDungeonInterruptChallengeRsp;

import java.util.Optional;

public class HandlerDungeonInterruptChallengeReq extends TypedPacketHandler<DungeonInterruptChallengeReq> {
    @Override
    public void handle(GameSession session, byte[] header, DungeonInterruptChallengeReq req) throws Exception {
        session.getPlayer().sendPacket(new PacketDungeonInterruptChallengeRsp(
            Optional.ofNullable(session.getPlayer().getScene().getChallenge())
                .filter(c -> c.isThisChallenge(req.getChallengeIndex(), req.getChallengeId(), req.getGroupId()))
                .map(WorldChallenge::fail)
                .orElse(false), req.getChallengeId(), req.getChallengeIndex(), req.getGroupId()));
    }
}
