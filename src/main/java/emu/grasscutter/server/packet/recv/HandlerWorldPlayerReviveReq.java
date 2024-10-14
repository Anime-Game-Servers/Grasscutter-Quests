package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketWorldPlayerReviveRsp;
import org.anime_game_servers.multi_proto.gi.messages.scene.WorldPlayerReviveReq;

public class HandlerWorldPlayerReviveReq extends TypedPacketHandler<WorldPlayerReviveReq> {
    @Override
    public void handle(GameSession session, byte[] header, WorldPlayerReviveReq req) throws Exception {
		session.getPlayer().getTeamManager().respawnTeam();
		session.send(new PacketWorldPlayerReviveRsp());
	}
}
