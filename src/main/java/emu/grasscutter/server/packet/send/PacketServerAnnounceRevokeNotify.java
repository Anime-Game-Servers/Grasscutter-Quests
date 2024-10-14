package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.announce.ServerAnnounceRevokeNotify;

import java.util.List;

public class PacketServerAnnounceRevokeNotify extends BaseTypedPacket<ServerAnnounceRevokeNotify> {
	public PacketServerAnnounceRevokeNotify(int tplId) {
        super(new ServerAnnounceRevokeNotify());
        proto.setConfigIdList(List.of(tplId));
	}
}
