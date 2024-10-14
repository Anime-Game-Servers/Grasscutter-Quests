package emu.grasscutter.data.excels;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import emu.grasscutter.game.props.BattlePassMissionRefreshType;
import emu.grasscutter.game.props.WatcherTriggerType;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.battle_pass.BattlePassMission;
import org.anime_game_servers.multi_proto.gi.messages.battle_pass.MissionStatus;

@ResourceType(name = {"BattlePassMissionExcelConfigData.json"})
@Getter
public class BattlePassMissionData extends GameResource {
    @Getter(onMethod = @__(@Override))
    private int id;
    private int addPoint;
    private int scheduleId;
    private int progress;
    private TriggerConfig triggerConfig;
    private BattlePassMissionRefreshType refreshType;

    private transient Set<Integer> mainParams;

    public WatcherTriggerType getTriggerType() {
        return this.getTriggerConfig().getTriggerType();
    }

    public boolean isCycleRefresh() {
        return getRefreshType() == null || getRefreshType() == BattlePassMissionRefreshType.BATTLE_PASS_MISSION_REFRESH_CYCLE_CROSS_SCHEDULE;
    }

    public boolean isValidRefreshType() {
        return getRefreshType() == null ||
            getRefreshType() == BattlePassMissionRefreshType.BATTLE_PASS_MISSION_REFRESH_CYCLE_CROSS_SCHEDULE ||
            getScheduleId() == 2701;
    }

    @Override
    public void onLoad() {
        if (this.getTriggerConfig() != null) {
            var params = getTriggerConfig().getParamList()[0];
            if ((params != null) && !params.isEmpty()) {
                this.mainParams = Arrays.stream(params.split("[:;,]")).map(Integer::parseInt).collect(Collectors.toSet());
            }
        }
    }

    @Getter
    public static class TriggerConfig {
        private WatcherTriggerType triggerType;
        private String[] paramList;
    }

    public BattlePassMission toProto() {
        val protoBuilder = new BattlePassMission(0, getId(), MissionStatus.MISSION_UNFINISHED);

        protoBuilder.setTotalProgress(this.getProgress());
        protoBuilder.setRewardBattlePassPoint(this.getAddPoint());
        protoBuilder.setMissionType(this.getRefreshType() == null ? 0 : this.getRefreshType().getValue());

        return protoBuilder;
    }
}
