package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.SceneForceLockNotify;

import java.util.List;

public class PacketSceneForceLockNotify extends BaseTypedPacket<SceneForceLockNotify> {
    public PacketSceneForceLockNotify(List<Integer> locked) {
        super(new SceneForceLockNotify());
        proto.setForceIdList(locked);
	}

	public PacketSceneForceLockNotify(int locked) {
        this(List.of(locked));
	}
}
