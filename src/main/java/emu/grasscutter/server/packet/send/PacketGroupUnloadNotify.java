package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.group.GroupUnloadNotify;

import java.util.List;

public class PacketGroupUnloadNotify extends BaseTypedPacket<GroupUnloadNotify> {

	public PacketGroupUnloadNotify(List<Integer> groupList) {
        super(new GroupUnloadNotify());
        proto.setGroupList(groupList);
	}
}
