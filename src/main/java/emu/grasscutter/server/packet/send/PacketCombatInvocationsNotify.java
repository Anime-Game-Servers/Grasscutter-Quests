package emu.grasscutter.server.packet.send;

import java.util.List;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.battle.CombatInvocationsNotify;
import org.anime_game_servers.multi_proto.gi.messages.battle.CombatInvokeEntry;

public class PacketCombatInvocationsNotify extends BaseTypedPacket<CombatInvocationsNotify> {

	public PacketCombatInvocationsNotify(CombatInvokeEntry entry) {
        this(List.of(entry));
	}

	public PacketCombatInvocationsNotify(List<CombatInvokeEntry> entries) {
		super(new CombatInvocationsNotify(entries), true);
	}

}
