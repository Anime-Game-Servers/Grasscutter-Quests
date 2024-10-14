package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketEntityAiSyncNotify;
import org.anime_game_servers.multi_proto.gi.messages.battle.EntityAiSyncNotify;

public class HandlerEntityAiSyncNotify extends TypedPacketHandler<EntityAiSyncNotify> {
	@Override
	public void handle(GameSession session, byte[] header, EntityAiSyncNotify notify) throws Exception {
		if (!notify.getLocalAvatarAlertedMonsterList().isEmpty()) {
			session.getPlayer().getScene().broadcastPacket(new PacketEntityAiSyncNotify(notify));
		}
	}

}
