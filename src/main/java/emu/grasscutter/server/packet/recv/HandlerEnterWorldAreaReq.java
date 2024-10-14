package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketEnterWorldAreaRsp;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.packet_head.PacketHead;
import org.anime_game_servers.multi_proto.gi.messages.scene.EnterWorldAreaReq;

public class HandlerEnterWorldAreaReq extends TypedPacketHandler<EnterWorldAreaReq> {
    @Override
    public void handle(GameSession session, byte[] header, EnterWorldAreaReq req) throws Exception {
		val head = PacketHead.parseBy(header, session.getVersion());
        session.send(new PacketEnterWorldAreaRsp(head.getClientSequenceId(), req));
		//session.send(new PacketScenePlayerLocationNotify(session.getPlayer()));
	}
}
