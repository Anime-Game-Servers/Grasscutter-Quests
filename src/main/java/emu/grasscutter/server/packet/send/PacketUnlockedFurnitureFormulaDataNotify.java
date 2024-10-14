package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.furniture.UnlockedFurnitureFormulaDataNotify;

import java.util.Set;

public class PacketUnlockedFurnitureFormulaDataNotify extends BaseTypedPacket<UnlockedFurnitureFormulaDataNotify> {
	public PacketUnlockedFurnitureFormulaDataNotify(Set<Integer> unlockList) {
        super(new UnlockedFurnitureFormulaDataNotify());
        proto.setFurnitureIdList(unlockList.stream().toList());
        proto.setAll(true);
	}
}
