package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.edit.HomeChangeEditModeRsp;

public class PacketHomeChangeEditModeRsp extends BaseTypedPacket<HomeChangeEditModeRsp> {
	public PacketHomeChangeEditModeRsp(boolean enterEditMode) {
        super(new HomeChangeEditModeRsp());
        proto.setEnterEditMode(enterEditMode);
	}
}
