package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.scene.SetEntityClientDataNotify;

public class HandlerSetEntityClientDataNotify extends TypedPacketHandler<SetEntityClientDataNotify> {
	@Override
    public void handle(GameSession session, byte[] header, SetEntityClientDataNotify req) throws Exception {
		// Skip if there is no one to broadcast it too
		if (session.getPlayer().getScene().getPlayerCount() <= 1) {
			return;
		}

		// Make sure packet is a valid proto before replaying it to the other players
        val notify = new SetEntityClientDataNotify();
        notify.setEntityClientData(req.getEntityClientData());
        notify.setEntityId(req.getEntityId());
        BaseTypedPacket<SetEntityClientDataNotify> packet = new BaseTypedPacket<>(notify, true) {
        };

		session.getPlayer().getScene().broadcastPacket(packet);
	}

}
