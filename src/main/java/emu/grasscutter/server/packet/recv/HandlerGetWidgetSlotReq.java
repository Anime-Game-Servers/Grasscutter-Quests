package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetWidgetSlotRsp;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.manage_slot.GetWidgetSlotReq;

public class HandlerGetWidgetSlotReq extends TypedPacketHandler<GetWidgetSlotReq> {
	@Override
    public void handle(GameSession session, byte[] header, GetWidgetSlotReq req) throws Exception {
		Player player = session.getPlayer();
		session.send(new PacketGetWidgetSlotRsp(player));
	}
}
