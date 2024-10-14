package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.child.AddQuestContentProgressRsp;

public class PacketAddQuestContentProgressRsp extends BaseTypedPacket<AddQuestContentProgressRsp> {

	public PacketAddQuestContentProgressRsp(int contentType) {
        super(new AddQuestContentProgressRsp());
        proto.setContentType(contentType);
	}
}
