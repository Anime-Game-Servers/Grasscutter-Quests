package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.furniture.UnlockedFurnitureSuiteDataNotify;

import java.util.Set;

public class PacketUnlockedFurnitureSuiteDataNotify extends BaseTypedPacket<UnlockedFurnitureSuiteDataNotify> {
	public PacketUnlockedFurnitureSuiteDataNotify(Set<Integer> unlockList) {
        super(new UnlockedFurnitureSuiteDataNotify());
        proto.setFurnitureSuiteIdList(unlockList.stream().toList());
        proto.setAll(true);
	}
}
