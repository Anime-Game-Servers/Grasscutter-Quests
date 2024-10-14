package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetActivityInfoRsp;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.GetActivityInfoReq;

import java.util.HashSet;

public class HandlerGetActivityInfoReq extends TypedPacketHandler<GetActivityInfoReq> {
	@Override
	public void handle(GameSession session, byte[] header, GetActivityInfoReq payload) throws Exception {
		session.send(new PacketGetActivityInfoRsp(
            new HashSet<>(payload.getActivityIdList()),
            session.getPlayer().getActivityManager()));
	}
}
