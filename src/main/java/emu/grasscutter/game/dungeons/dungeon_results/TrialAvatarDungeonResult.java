package emu.grasscutter.game.dungeons.dungeon_results;

import emu.grasscutter.data.excels.DungeonData;
import emu.grasscutter.game.dungeons.DungeonEndStats;
import emu.grasscutter.game.player.Player;
import lombok.Builder;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.activity.trial.TrialAvatarFirstPassDungeonNotify;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.progression.DungeonSettleNotify;

public class TrialAvatarDungeonResult extends BaseDungeonResult {
    int trialCharacterIndexId;

    @Builder(builderMethodName = "TrialAvatarBuilder", setterPrefix = "set")
    private TrialAvatarDungeonResult(DungeonData dungeonData, DungeonEndStats dungeonStats, Player player, int trialCharacterIndexId) {
        super(dungeonData, dungeonStats, player);
        this.trialCharacterIndexId = trialCharacterIndexId;
    }

    @Override
    protected void onProto(DungeonSettleNotify builder) {
        if (getDungeonStats().dungeonResult() == DungeonEndReason.COMPLETED) { // TODO check if it's the first pass(?)
            val trialAvatarFirstPassDungeonNotify = new TrialAvatarFirstPassDungeonNotify();
            trialAvatarFirstPassDungeonNotify.setTrialAvatarIndexId(this.trialCharacterIndexId);
            builder.setDetail(new DungeonSettleNotify.Detail.TrialAvatarFirstPassDungeonNotify(trialAvatarFirstPassDungeonNotify));
        }
    }
}
