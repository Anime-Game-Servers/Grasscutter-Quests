package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.PropValue;
import org.anime_game_servers.multi_proto.gi.messages.player.PlayerDataNotify;

import java.util.stream.Collectors;

public class PacketPlayerDataNotify extends BaseTypedPacket<PlayerDataNotify> {

	public PacketPlayerDataNotify(Player player) {
		super(new PlayerDataNotify(), 2);

        proto.setNickName(player.getNickname());
        proto.setServerTime(System.currentTimeMillis());
        proto.setFirstLoginToday(true);
        proto.setRegionId(player.getRegionId());
        proto.setPropMap(
            player.getProperties().entrySet().stream().collect(
                Collectors.toMap(e->e.getKey(), e -> new PropValue(e.getKey(), e.getValue(), new PropValue.Value.Ival(e.getValue())))
            )
        );

		/*player.getProperties().forEach((key, value) -> {
			p.putPropMap(key, PropValue.newBuilder().setType(key).setIval(value).setVal(value).build());
		});

		this.setData(p.build());*/
	}
}
