package emu.grasscutter.game.dungeons.dungeon_results;

import emu.grasscutter.data.excels.DungeonData;
import emu.grasscutter.game.dungeons.DungeonEndStats;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.player.Player;
import lombok.Builder;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.progression.DungeonSettleNotify;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.run.ContinueStateType;
import org.anime_game_servers.multi_proto.gi.messages.spiral_abyss.run.TowerLevelEndNotify;

import java.util.List;
import java.util.stream.IntStream;

public class TowerResult extends BaseDungeonResult {
    private final boolean hasNextFloor;
    private final boolean hasNextLevel;
    private final int nextFloorId;
    private final int stars;
    private final List<GameItem> rewardItems;

    @Builder(builderMethodName = "TowerBuilder", setterPrefix = "set")
    public TowerResult(DungeonData dungeonData, DungeonEndStats dungeonStats, Player player,
                       boolean hasNextLevel, boolean hasNextFloor, int nextFloorId, int stars,
                       List<GameItem> rewardItems) {
        super(dungeonData, dungeonStats, player);
        this.hasNextFloor = hasNextFloor;
        this.hasNextLevel = hasNextLevel;
        this.nextFloorId = this.hasNextLevel ? 0 : nextFloorId;
        this.stars = stars;
        this.rewardItems = rewardItems;
    }

    @Override
    protected void onProto(DungeonSettleNotify builder) {
        val success = getDungeonStats().getDungeonResult().isSuccess();

        val towerLevelEndNotify = new TowerLevelEndNotify();
        towerLevelEndNotify.setSuccess(success);
        towerLevelEndNotify.setContinueState(success && this.hasNextFloor ? this.hasNextLevel ?
            ContinueStateType.CONTINUE_STATE_CAN_ENTER_NEXT_LEVEL :
            ContinueStateType.CONTINUE_STATE_CAN_ENTER_NEXT_FLOOR :
            ContinueStateType.CONTINUE_STATE_CAN_NOT_CONTINUE);
        towerLevelEndNotify.setFinishedStarCondList(IntStream.rangeClosed(1, this.stars).boxed().toList());
        towerLevelEndNotify.setNextFloorId(this.hasNextFloor ? this.nextFloorId : 0);
        towerLevelEndNotify.setRewardItemList(this.rewardItems.stream()
            .map(GameItem::toItemParam)
            .toList());

        builder.setDetail(new DungeonSettleNotify.Detail.TowerLevelEndNotify(towerLevelEndNotify));
    }
}
