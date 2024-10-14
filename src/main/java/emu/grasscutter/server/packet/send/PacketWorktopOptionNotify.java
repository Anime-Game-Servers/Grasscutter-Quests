package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.entity.gadget.GadgetWorktop;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.gadget.WorktopOptionNotify;

import java.util.ArrayList;

public class PacketWorktopOptionNotify extends BaseTypedPacket<WorktopOptionNotify> {

	public PacketWorktopOptionNotify(EntityGadget gadget) {
        super(new WorktopOptionNotify());
        proto.setGadgetEntityId(gadget.getId());
		if (gadget.getContent() instanceof GadgetWorktop worktop) {
            proto.setOptionIdList(new ArrayList<>(worktop.getWorktopOptions()));
		}
	}
}
