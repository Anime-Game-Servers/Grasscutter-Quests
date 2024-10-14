package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.battle.AiSyncInfo;
import org.anime_game_servers.multi_proto.gi.messages.battle.EntityAiSyncNotify;

public class PacketEntityAiSyncNotify extends BaseTypedPacket<EntityAiSyncNotify> {

	public PacketEntityAiSyncNotify(EntityAiSyncNotify notify) {
		super(new EntityAiSyncNotify(), true);

        val aiInfo = notify.getLocalAvatarAlertedMonsterList().stream()
            .map(monsterId -> new AiSyncInfo(monsterId,true))
            .toList();
		proto.setInfoList(aiInfo);
	}
}
