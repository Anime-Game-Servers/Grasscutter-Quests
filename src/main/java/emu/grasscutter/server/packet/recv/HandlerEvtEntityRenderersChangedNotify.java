package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketEvtEntityRenderersChangedNotify;
import org.anime_game_servers.multi_proto.gi.messages.battle.event.EvtEntityRenderersChangedNotify;

public class HandlerEvtEntityRenderersChangedNotify extends TypedPacketHandler<EvtEntityRenderersChangedNotify> {

	@Override
	public void handle(GameSession session, byte[] header, EvtEntityRenderersChangedNotify req) throws Exception {

        switch (req.getForwardType()) {
            case FORWARD_TO_ALL ->
                session.getPlayer().getScene().broadcastPacket(new PacketEvtEntityRenderersChangedNotify(req));
            case FORWARD_TO_ALL_EXCEPT_CUR ->
                session.getPlayer().getScene().broadcastPacketToOthers(session.getPlayer(), new PacketEvtEntityRenderersChangedNotify(req));
            case FORWARD_TO_HOST ->
                session.getPlayer().getScene().getWorld().getHost().sendPacket(new PacketEvtEntityRenderersChangedNotify(req));
        }

	}

}
