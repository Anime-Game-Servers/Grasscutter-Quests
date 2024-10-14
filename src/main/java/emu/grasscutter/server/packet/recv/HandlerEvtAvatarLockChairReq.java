package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketEvtAvatarLockChairRsp;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.battle.event.EvtAvatarLockChairReq;
import org.anime_game_servers.multi_proto.gi.messages.packet_head.PacketHead;

public class HandlerEvtAvatarLockChairReq extends TypedPacketHandler<EvtAvatarLockChairReq> {

    @Override
    public void handle(GameSession session, byte[] header, EvtAvatarLockChairReq req) throws Exception {
        val head = PacketHead.parseBy(header, session.getVersion());

        EntityAvatar entityAvatar = session.getPlayer().getTeamManager().getCurrentAvatarEntity();

        session.send(new PacketEvtAvatarLockChairRsp(head.getClientSequenceId(), entityAvatar, req));
    }

}
