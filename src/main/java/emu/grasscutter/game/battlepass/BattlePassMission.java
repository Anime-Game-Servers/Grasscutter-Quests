package emu.grasscutter.game.battlepass;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Transient;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.BattlePassMissionData;
import emu.grasscutter.game.props.BattlePassMissionStatus;

@Entity
public class BattlePassMission {
	private int id;
	private int progress;
	private BattlePassMissionStatus status;

	@Transient
	private BattlePassMissionData data;

	@Deprecated // Morphia only
	public BattlePassMission() {}

	public BattlePassMission(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public BattlePassMissionData getData() {
		if (this.data == null) {
			this.data = GameData.getBattlePassMissionDataMap().get(getId());
		}
		return this.data;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int value) {
		this.progress = value;
	}

	public void addProgress(int addProgress, int maxProgress) {
		this.progress = Math.min(addProgress + this.progress, maxProgress);
	}

	public BattlePassMissionStatus getStatus() {
		if (status == null) status = BattlePassMissionStatus.MISSION_STATUS_UNFINISHED;
		return status;
	}

	public void setStatus(BattlePassMissionStatus status) {
		this.status = status;
	}

	public boolean isFinshed() {
		return getStatus().getValue() >= 2;
	}

	public org.anime_game_servers.multi_proto.gi.messages.battle_pass.BattlePassMission toProto() {
		var proto = new org.anime_game_servers.multi_proto.gi.messages.battle_pass.BattlePassMission(getProgress(), getId(),
            getStatus().getMissionStatus());

        proto.setTotalProgress(getData().getRefreshType() == null ? 0 : getData().getRefreshType().getValue());
        proto.setRewardBattlePassPoint(getData().getAddPoint());
        proto.setTotalProgress(getData().getProgress());

        return proto;
	}
}
