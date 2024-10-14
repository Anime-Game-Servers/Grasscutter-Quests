package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketHomeBasicInfoNotify;
import emu.grasscutter.server.packet.send.PacketHomeChangeEditModeRsp;
import emu.grasscutter.server.packet.send.PacketHomeComfortInfoNotify;
import emu.grasscutter.server.packet.send.PacketHomePreChangeEditModeNotify;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.edit.HomeChangeEditModeReq;

public class HandlerHomeChangeEditModeReq extends TypedPacketHandler<HomeChangeEditModeReq> {
	@Override
    public void handle(GameSession session, byte[] header, HomeChangeEditModeReq req) throws Exception {
        session.send(new PacketHomePreChangeEditModeNotify(req.isEnterEditMode()));
        session.send(new PacketHomeBasicInfoNotify(session.getPlayer(), req.isEnterEditMode()));
		session.send(new PacketHomeComfortInfoNotify(session.getPlayer()));
        session.send(new PacketHomeChangeEditModeRsp(req.isEnterEditMode()));
	}
}
