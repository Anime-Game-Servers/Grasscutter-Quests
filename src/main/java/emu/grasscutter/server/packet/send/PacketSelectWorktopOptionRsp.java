package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.gadget.SelectWorktopOptionRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketSelectWorktopOptionRsp extends BaseTypedPacket<SelectWorktopOptionRsp> {

	public PacketSelectWorktopOptionRsp(int entityId, int optionId) {
		super(new SelectWorktopOptionRsp(Retcode.RET_SUCC, entityId, optionId));
	}
}
