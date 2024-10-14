package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.SceneForceUnlockNotify;

import java.util.List;

public class PacketSceneForceUnlockNotify extends BaseTypedPacket<SceneForceUnlockNotify> {
	public PacketSceneForceUnlockNotify(List<Integer> unlocked, boolean isAdd) {
		super(new SceneForceUnlockNotify(unlocked, isAdd));
	}

	public PacketSceneForceUnlockNotify(int unlocked, boolean isAdd) {
		super(new SceneForceUnlockNotify(List.of(unlocked), isAdd));
	}
}
