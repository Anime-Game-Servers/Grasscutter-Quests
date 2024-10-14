package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.world.World;
import emu.grasscutter.net.packet.BaseTypedPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.PropValue;
import org.anime_game_servers.multi_proto.gi.messages.world.WorldDataNotify;

public class PacketWorldDataNotify extends BaseTypedPacket<WorldDataNotify> {

	public PacketWorldDataNotify(World world) {
		super(new WorldDataNotify());

		int worldLevel = world.getWorldLevel();
		int isMp = world.isMultiplayer() ? 1 : 0;

        val props = new Int2ObjectOpenHashMap<PropValue>();
        props.put(1, new PropValue(1, worldLevel, new PropValue.Value.Ival(worldLevel)));
        props.put(1, new PropValue(2, isMp, new PropValue.Value.Ival(isMp)));

        proto.setWorldPropMap(props);
	}
}
