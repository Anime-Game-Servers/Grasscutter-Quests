package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.entity.EntityBaseGadget;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.gadget.GadgetInteractRsp;
import org.anime_game_servers.multi_proto.gi.messages.gadget.InterOpType;
import org.anime_game_servers.multi_proto.gi.messages.gadget.InteractType;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketGadgetInteractRsp extends BaseTypedPacket<GadgetInteractRsp> {
	public PacketGadgetInteractRsp(EntityBaseGadget gadget, InteractType interact) {
		this(gadget, interact, null);
	}

	public PacketGadgetInteractRsp(EntityBaseGadget gadget, InteractType interact, InterOpType opType) {
		super(new GadgetInteractRsp(Retcode.RET_SUCC, gadget.getId()));

		proto.setGadgetId(gadget.getGadgetId());
        proto.setInteractType(interact);

		if (opType != null) {
			proto.setOpType(opType);
		}
	}

	public PacketGadgetInteractRsp() {
		super(new GadgetInteractRsp(Retcode.RET_SVR_ERROR));
	}
}
