package emu.grasscutter.server.packet.send;

import java.util.List;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.ability.AbilityInvokeEntry;
import org.anime_game_servers.multi_proto.gi.messages.ability.ClientAbilityInitFinishNotify;

public class PacketClientAbilityInitFinishNotify extends BaseTypedPacket<ClientAbilityInitFinishNotify> {

	public PacketClientAbilityInitFinishNotify(List<AbilityInvokeEntry> entries) {
		super(new ClientAbilityInitFinishNotify(), true);

		int entityId = 0;

		if (!entries.isEmpty()) {
			AbilityInvokeEntry entry = entries.get(0);
			entityId = entry.getEntityId();
		}

        proto.setEntityId(entityId);
        proto.setInvokes(entries);
	}
}
