package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.multiplayer.PlayerApplyEnterMpRsp;

public class PacketPlayerApplyEnterMpRsp extends BaseTypedPacket<PlayerApplyEnterMpRsp> {
	public PacketPlayerApplyEnterMpRsp(int targetUid) {
        super(new PlayerApplyEnterMpRsp());
        proto.setTargetUid(targetUid);
	}
}
