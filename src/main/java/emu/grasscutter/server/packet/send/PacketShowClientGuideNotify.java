package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.ShowClientGuideNotify;

public class PacketShowClientGuideNotify extends BaseTypedPacket<ShowClientGuideNotify> {
	public PacketShowClientGuideNotify(String guideName) {
        super(new ShowClientGuideNotify(), true);
        proto.setGuideName(guideName);
	}
}
