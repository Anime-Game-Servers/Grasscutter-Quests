package emu.grasscutter.game.dungeons.settle_listeners;

import emu.grasscutter.game.activity.trialavatar.TrialAvatarActivityHandler;
import emu.grasscutter.game.dungeons.DungeonEndStats;
import emu.grasscutter.game.dungeons.DungeonManager;
import emu.grasscutter.game.dungeons.dungeon_results.BaseDungeonResult;
import emu.grasscutter.game.dungeons.dungeon_results.TrialAvatarDungeonResult;
import emu.grasscutter.server.packet.send.PacketDungeonSettleNotify;
import lombok.val;
import org.anime_game_servers.game_data_models.data.activity.ActivityType;

public class TrialAvatarDungeonSettleListener implements DungeonSettleListener{
    @Override
    public void onDungeonSettle(DungeonManager dungeonManager, BaseDungeonResult.DungeonEndReason endReason) {
        val scene = dungeonManager.getScene();
        val hostPlayer = scene.getWorld().getHost();
        val time = scene.getSceneTimeSeconds() - dungeonManager.getStartSceneTime();

        hostPlayer.sendPacket(new PacketDungeonSettleNotify(TrialAvatarDungeonResult.TrialAvatarBuilder()
            .setDungeonData(dungeonManager.getDungeonData())
            .setDungeonStats(new DungeonEndStats(scene.getKilledMonsterCount(), time, 0, endReason))
            .setPlayer(hostPlayer)
            .setTrialCharacterIndexId(hostPlayer.getActivityManager()
                .getActivityHandlerAs(ActivityType.NEW_ACTIVITY_TRIAL_AVATAR, TrialAvatarActivityHandler.class)
                .map(TrialAvatarActivityHandler::getSelectedTrialAvatarIndex).orElse(0))
            .build()));
    }
}
