package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.forge.ForgeFormulaDataNotify;

public class PacketForgeFormulaDataNotify extends BaseTypedPacket<ForgeFormulaDataNotify> {
	public PacketForgeFormulaDataNotify(int itemId) {
        super(new ForgeFormulaDataNotify());
        proto.setForgeId(itemId);
        proto.setLocked(false);
	}
}
