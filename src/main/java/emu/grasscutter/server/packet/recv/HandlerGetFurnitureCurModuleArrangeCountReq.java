package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketFurnitureCurModuleArrangeCountNotify;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.furniture.GetFurnitureCurModuleArrangeCountReq;

public class HandlerGetFurnitureCurModuleArrangeCountReq extends TypedPacketHandler<GetFurnitureCurModuleArrangeCountReq> {
	@Override
    public void handle(GameSession session, byte[] header, GetFurnitureCurModuleArrangeCountReq req) throws Exception {
		session.send(new PacketFurnitureCurModuleArrangeCountNotify());
	}
}
