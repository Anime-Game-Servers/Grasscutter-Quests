package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.ShowCommonTipsNotify;

public class PacketShowCommonTipsNotify extends BaseTypedPacket<ShowCommonTipsNotify> {
	public PacketShowCommonTipsNotify(String title, String content, int closeTime) {
        super(new ShowCommonTipsNotify());
        proto.setTitle(title);
        proto.setContent(content);
        proto.setCloseTime(closeTime);
	}
}
