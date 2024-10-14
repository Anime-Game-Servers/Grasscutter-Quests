package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.BattlePassMissionData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.battle_pass.BattlePassAllDataNotify;


public class PacketBattlePassAllDataNotify extends BaseTypedPacket<BattlePassAllDataNotify> {
    public PacketBattlePassAllDataNotify(Player player) {
        super(new BattlePassAllDataNotify());

        proto.setHaveCurSchedule(true);
        proto.setCurSchedule(player.getBattlePassManager().getScheduleProto());

        val missions = GameData.getBattlePassMissionDataMap().values().stream()
            // Don't send invalid refresh types
            .filter(BattlePassMissionData::isValidRefreshType)
            .map(missionData -> {
                // Check if player has mission in bp manager. If not, then add an empty proto from the mission data
                if (player.getBattlePassManager().hasMission(missionData.getId())) {
                    return player.getBattlePassManager().loadMissionById(missionData.getId()).toProto();
                } else {
                    return missionData.toProto();
                }
            }).toList();
        proto.setMissionList(missions);
    }
}
