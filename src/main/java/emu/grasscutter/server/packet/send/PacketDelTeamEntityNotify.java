package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.battle.DelTeamEntityNotify;

import java.util.List;

public class PacketDelTeamEntityNotify extends BaseTypedPacket<DelTeamEntityNotify> {
	public PacketDelTeamEntityNotify(int sceneId, int teamEntityId) {
        this(sceneId, List.of(teamEntityId));
	}

    public PacketDelTeamEntityNotify(int sceneId, List<Integer> list) {
        super(new DelTeamEntityNotify());
        proto.setSceneId(sceneId);
        proto.setDelEntityIdList(list);
	}
}
