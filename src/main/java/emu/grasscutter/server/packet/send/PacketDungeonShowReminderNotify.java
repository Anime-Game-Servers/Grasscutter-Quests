package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.DungeonShowReminderNotify;

public class PacketDungeonShowReminderNotify extends BaseTypedPacket<DungeonShowReminderNotify> {

	public PacketDungeonShowReminderNotify(int reminderId) {
        super(new DungeonShowReminderNotify());
        proto.setReminderId(reminderId);
	}
}
