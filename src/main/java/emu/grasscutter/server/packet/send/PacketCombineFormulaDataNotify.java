package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.combine.CombineFormulaDataNotify;

public class PacketCombineFormulaDataNotify extends BaseTypedPacket<CombineFormulaDataNotify> {
	public PacketCombineFormulaDataNotify(int combineId) {
        super(new CombineFormulaDataNotify());
        proto.setCombineId(combineId);
        proto.setLocked(false);
	}
}
