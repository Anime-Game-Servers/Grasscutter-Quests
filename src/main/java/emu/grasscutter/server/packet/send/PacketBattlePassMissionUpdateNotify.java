package emu.grasscutter.server.packet.send;

import java.util.Collection;
import java.util.List;

import emu.grasscutter.game.battlepass.BattlePassMission;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.battle_pass.BattlePassMissionUpdateNotify;

public class PacketBattlePassMissionUpdateNotify extends BaseTypedPacket<BattlePassMissionUpdateNotify> {

    public PacketBattlePassMissionUpdateNotify(BattlePassMission mission) {
        this(List.of(mission.toProto()));
    }

    public PacketBattlePassMissionUpdateNotify(Collection<BattlePassMission> missions) {
        this(missions.stream().map(BattlePassMission::toProto).toList());
    }

    protected PacketBattlePassMissionUpdateNotify(List<org.anime_game_servers.multi_proto.gi.messages.battle_pass.BattlePassMission> protoMissions) {
        super(new BattlePassMissionUpdateNotify(protoMissions));
    }

}
