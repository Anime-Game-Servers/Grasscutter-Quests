package emu.grasscutter.game.battlepass;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Transient;
import emu.grasscutter.data.excels.BattlePassMissionData;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.battle_pass.BattlePassRewardTag;
import org.anime_game_servers.multi_proto.gi.messages.battle_pass.BattlePassUnlockStatus;

@Entity
public class BattlePassReward {
	private int level;
	private int rewardId;
	private boolean paid;

	@Transient
	private BattlePassMissionData data;

	@Deprecated // Morphia only
	public BattlePassReward() {}

	public BattlePassReward(int level, int rewardId, boolean paid) {
		this.level = level;
		this.rewardId = rewardId;
		this.paid = paid;
	}

	public int getLevel() {
		return level;
	}

	public int getRewardId() {
		return rewardId;
	}

	public boolean isPaid() {
		return paid;
	}

	public BattlePassRewardTag toProto() {
        val status = this.paid ? BattlePassUnlockStatus.BATTLE_PASS_UNLOCK_PAID : BattlePassUnlockStatus.BATTLE_PASS_UNLOCK_FREE;
		return new BattlePassRewardTag(this.level, this.rewardId, status);
	}
}
