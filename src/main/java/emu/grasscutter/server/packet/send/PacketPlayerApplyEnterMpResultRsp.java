package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.multiplayer.PlayerApplyEnterMpResultRsp;

public class PacketPlayerApplyEnterMpResultRsp extends BaseTypedPacket<PlayerApplyEnterMpResultRsp> {
	public PacketPlayerApplyEnterMpResultRsp(int applyUid, boolean isAgreed) {
        super(new PlayerApplyEnterMpResultRsp());
        proto.setApplyUid(applyUid);
        proto.setAgreed(isAgreed);
	}
}
