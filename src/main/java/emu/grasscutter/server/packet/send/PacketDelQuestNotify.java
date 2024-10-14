package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.child.QuestDelNotify;

public class PacketDelQuestNotify extends BaseTypedPacket<QuestDelNotify> {

	public PacketDelQuestNotify(int questId) {
        super(new QuestDelNotify());
        proto.setQuestId(questId);
	}

}
