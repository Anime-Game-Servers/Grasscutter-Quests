package emu.grasscutter.game.dungeons;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.DungeonData;
import emu.grasscutter.data.excels.DungeonElementChallengeData;
import emu.grasscutter.data.excels.DungeonPassConfigData;
import emu.grasscutter.game.activity.trialavatar.TrialAvatarActivityHandler;
import emu.grasscutter.game.dungeons.dungeon_results.BaseDungeonResult;
import emu.grasscutter.game.dungeons.enums.DungeonPassConditionType;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.ActionReason;
import emu.grasscutter.game.props.ActivityType;
import emu.grasscutter.game.props.WatcherTriggerType;
import emu.grasscutter.game.quest.enums.LogicType;
import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.server.packet.send.PacketDungeonDataNotify;
import emu.grasscutter.server.packet.send.PacketDungeonReviseLevelNotify;
import emu.grasscutter.server.packet.send.PacketDungeonWayPointNotify;
import emu.grasscutter.server.packet.send.PacketGadgetAutoPickDropInfoNotify;
import emu.grasscutter.utils.Position;
import emu.grasscutter.utils.Utils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.avatar.GrantReason;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.WeeklyBossResinDiscountInfo;
import org.anime_game_servers.gi_lua.models.ScriptArgs;
import org.anime_game_servers.gi_lua.models.constants.EventType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * TODO handle time limits
 * TODO handle respawn points
 * TODO handle team wipes and respawns
 * TODO check monster level and levelConfigMap
 */
public class DungeonManager {

    @Getter private final Scene scene;
    @Getter private final DungeonData dungeonData;
    @Getter private final DungeonPassConfigData passConfigData;

    @Getter private final int[] finishedConditions;
    @Getter private final IntSet rewardedPlayers = new IntOpenHashSet();
    private final Set<Integer> activeDungeonWayPoints = new HashSet<>();
    private boolean ended = false;
    private int newestWayPoint = 0;
    @Getter private int startSceneTime = 0;
    @Getter @Setter private int delayExitTaskId = -1;

    public DungeonManager(@NonNull Scene scene, @NonNull DungeonData dungeonData) {
        this.scene = scene;
        this.dungeonData = dungeonData;
        this.passConfigData = GameData.getDungeonPassConfigDataMap().get(dungeonData.getPassCond());
        this.finishedConditions = new int[this.passConfigData.getConds().size()];
    }

    public void triggerEvent(DungeonPassConditionType conditionType, int... params) {
        if (this.ended) return;

        this.passConfigData.getConds().stream().filter(cond -> cond.getCondType() == conditionType)
            .filter(cond -> getScene().getWorld().getServer().getDungeonSystem().triggerCondition(cond, params))
            .forEach(cond -> this.finishedConditions[this.passConfigData.getConds().indexOf(cond)] = 1);

        if (isFinishedSuccessfully()) finishDungeon();
    }

    public boolean isFinishedSuccessfully() {
        return LogicType.calculate(this.passConfigData.getLogicType(), this.finishedConditions);
    }

    public int getLevelForMonster(int id) {
        //TODO should use levelConfigMap? and how?
        return this.dungeonData.getShowLevel();
    }

    public boolean activateRespawnPoint(int pointId) {
        val respawnPoint = GameData.getScenePointEntryById(this.scene.getId(), pointId);

        // getter depend on this check being done here
        if (respawnPoint == null) {
            Grasscutter.getLogger().warn("trying to activate unknown respawn point {}", pointId);
            return false;
        }

        this.scene.broadcastPacket(new PacketDungeonWayPointNotify(this.activeDungeonWayPoints.add(pointId), this.activeDungeonWayPoints));
        this.newestWayPoint = pointId;
        Grasscutter.getLogger().debug("[unimplemented respawn] activated respawn point {}", pointId);
        return true;
    }

    @Nullable
    public Position getRespawnLocation() {
        // validity is checked before setting it, so if != 0 its always valid
        if(newestWayPoint == 0)
            return null;
        return GameData.getScenePointEntryById(this.scene.getId(), this.newestWayPoint)
            .getPointData()
            .getTransPosWithFallback();
    }

    public Position getRespawnRotation() {
        // validity is checked before setting it, so if != 0 its always valid
        if(newestWayPoint == 0)
            return null;
        return GameData.getScenePointEntryById(this.scene.getId(), this.newestWayPoint)
            .getPointData()
            .getTransRotWithFallback();
    }

    private boolean hasRewards(){
        //TODO check dungeonData.passRewardId and dungeondata.passRewardPreviewId only as fallback
        return this.dungeonData.getRewardPreviewData() != null && this.dungeonData.getRewardPreviewData().getPreviewItems().length > 0;
    }

    private boolean hasPlayerClaimedRewards(Player player){
        return this.rewardedPlayers.contains(player.getUid());
    }

    public boolean getStatueDrops(Player player, boolean useCondensed, int groupId) {
        // checks before claiming rewards
        if (!isFinishedSuccessfully() || !hasRewards() || hasPlayerClaimedRewards(player))
            return false;


        // check if player has enough currency to claim and pay, otherwise fail
        if (!handleCost(player, useCondensed))
            return false;

        // Get and roll rewards.
        val rewards = rollRewards(useCondensed);
        // Add rewards to player and send notification.
        player.getInventory().addItems(rewards, ActionReason.DungeonStatueDrop);
        player.sendPacket(new PacketGadgetAutoPickDropInfoNotify(rewards));
        this.rewardedPlayers.add(player.getUid());
        this.scene.getScriptManager().callEvent(new ScriptArgs(groupId, EventType.EVENT_DUNGEON_REWARD_GET));
        player.getDungeonEntryManager().updateDungeonEntryInfo(this.dungeonData);
        return true;
    }

    public boolean handleCost(Player player, boolean useCondensed) {
        final int resinCost = this.dungeonData.getStatueCostCount() != 0 ? this.dungeonData.getStatueCostCount() : 20;

        if (useCondensed) {
            // Check if condensed resin is usable here.
            // For this, we use the following logic for now:
            // The normal resin cost of the dungeon has to be 20.
            if (resinCost != 20) return false;

            // Spend the condensed resin and only proceed if the transaction succeeds.
            return player.getResinManager().useCondensedResin(1);
        } else if (this.dungeonData.getStatueCostID() == 106) {
            // Spend the resin and only proceed if the transaction succeeds.
            return player.getResinManager().useResin(resinCost);
        } //TODO should we maybe support other currencies here?
        return true;
    }

    private List<GameItem> rollRewards(boolean useCondensed) {
        val rewards = new ArrayList<GameItem>();
        final int dungeonId = this.dungeonData.getId();
        // If we have specific drop data for this dungeon, we use it.
        if (GameData.getDungeonDropDataMap().containsKey(dungeonId)) {
            val dropEntries = GameData.getDungeonDropDataMap().get(dungeonId);

            // Roll for each drop group.
            for (val entry : dropEntries) {
                // Determine the number of drops we get for this entry.
                int start = entry.getCounts().get(0);
                int end = entry.getCounts().get(entry.getCounts().size() - 1);
                var candidateAmounts = IntStream.range(start, end + 1).boxed().collect(Collectors.toList());

                int amount = Utils.drawRandomListElement(candidateAmounts, entry.getProbabilities());

                if (useCondensed) {
                    amount += Utils.drawRandomListElement(candidateAmounts, entry.getProbabilities());
                }

                // Double rewards in multi play mode, if specified.
                if (entry.isMpDouble() && this.getScene().getPlayerCount() > 1) {
                    amount *= 2;
                }

                // Roll items for this group.
                // Here, we have to handle stacking, or the client will not display results correctly.
                // For now, we use the following logic: If the possible drop item are a list of multiple items,
                // we roll them separately. If not, we stack them. This should work out in practice, at least
                // for the currently existing set of dungeons.
                if (entry.getItems().size() == 1) {
                    rewards.add(new GameItem(entry.getItems().get(0), amount));
                } else {
                    for (int i = 0; i < amount; i++) {
                        int itemId = Utils.drawRandomListElement(entry.getItems(), entry.getItemProbabilities());
                        rewards.add(new GameItem(itemId, 1));
                    }
                }
            }
        }
        // Otherwise, we fall back to the preview data.
        else {
            Grasscutter.getLogger().info("No drop data found or dungeon {}, falling back to preview data ...", dungeonId);
            Arrays.stream(this.dungeonData.getRewardPreviewData().getPreviewItems())
                .map(param -> new GameItem(param.getId(), Math.max(param.getCount(), 1)))
                .forEach(rewards::add);
        }

        return rewards;
    }

    private void applyTrialTeam(Player player) {
        if (this.dungeonData == null) return;

        switch (this.dungeonData.getType()) {
            // case DUNGEON_PLOT is handled by quest execs
            case DUNGEON_ACTIVITY -> {
                switch (this.dungeonData.getPlayType()) {
                    case DUNGEON_PLAY_TYPE_TRIAL_AVATAR ->
                        player.getActivityManager().getActivityHandlerAs(
                                ActivityType.NEW_ACTIVITY_TRIAL_AVATAR, TrialAvatarActivityHandler.class)
                            .map(TrialAvatarActivityHandler::getBattleAvatarsList)
                            .ifPresent(battleAvatars -> player.addTrialAvatarsForDungeon(
                                battleAvatars, GrantReason.GRANT_BY_TRIAL_AVATAR_ACTIVITY));

                    case DUNGEON_PLAY_TYPE_MIST_TRIAL -> {} // TODO
                }
            }
            case DUNGEON_ELEMENT_CHALLENGE ->
                Optional.ofNullable(GameData.getDungeonElementChallengeDataMap().get(getDungeonData().getId()))
                    .map(DungeonElementChallengeData::getTrialAvatarId)
                    .ifPresent(trialAvatarId -> player.addTrialAvatarsForDungeon(
                        trialAvatarId, GrantReason.GRANT_BY_DUNGEON_ELEMENT_CHALLENGE));
        }
        if (player.getTeamManager().isUseTrialTeam()){
            player.getTeamManager().updateTeamEntities(false);
        }
    }

    public void startDungeon() {
        this.startSceneTime = this.scene.getSceneTimeSeconds();
        this.scene.getPlayers().forEach(p -> {
            p.getQuestManager().queueEvent(QuestContent.QUEST_CONTENT_ENTER_DUNGEON, this.dungeonData.getId());
            applyTrialTeam(p);
        });
    }

    public void finishDungeon() {
        notifyEndDungeon(true);
        endDungeon(BaseDungeonResult.DungeonEndReason.COMPLETED);
    }

    public void notifyEndDungeon(boolean successfully) {
        this.scene.getPlayers().forEach(p -> {
            // Quest trigger
            p.getQuestManager().queueEvent(successfully ?
                    QuestContent.QUEST_CONTENT_FINISH_DUNGEON : QuestContent.QUEST_CONTENT_FAIL_DUNGEON,
                this.dungeonData.getId());

            // Battle pass trigger
            if (this.dungeonData.getType().isCountsToBattlepass() && successfully) {
                p.getBattlePassManager().triggerMission(WatcherTriggerType.TRIGGER_FINISH_DUNGEON);
            }
            if(dungeonData.getPassJumpDungeon() > 0){
                p.getServer().getDungeonSystem().enterDungeon(p, 0, dungeonData.getPassJumpDungeon());
            }
        });
        this.scene.getScriptManager().callEvent(new ScriptArgs(0, EventType.EVENT_DUNGEON_SETTLE, successfully ? 1 : 0));
    }

    public void quitDungeon() {
        notifyEndDungeon(false);
        endDungeon(BaseDungeonResult.DungeonEndReason.QUIT);
    }

    public void failDungeon() {
        notifyEndDungeon(false);
        endDungeon(BaseDungeonResult.DungeonEndReason.FAILED);
    }

    public void endDungeon(BaseDungeonResult.DungeonEndReason endReason) {
        if (scene.getDungeonSettleListeners() != null) {
            scene.getDungeonSettleListeners().forEach(o -> o.onDungeonSettle(this, endReason));
        }
        this.ended = true;
    }

    public void sendDungeonInfoPacket() {
        this.scene.broadcastPacket(new PacketDungeonReviseLevelNotify(this.dungeonData));
        // TODO, this probably has something to do with level config map
        this.scene.broadcastPacket(new PacketDungeonDataNotify(this.dungeonData));
    }
    public void restartDungeon() {
        this.scene.setKilledMonsterCount(0);
        this.rewardedPlayers.clear();
        Arrays.fill(finishedConditions, 0);
        this.ended = false;
        this.activeDungeonWayPoints.clear();
    }

    public void cleanUpScene() {
        this.scene.setDungeonManager(null);
        this.scene.setKilledMonsterCount(0);
    }

    /**
     * Get weekly boss chest information for qualified player
     * */
    public Map<Integer, WeeklyBossResinDiscountInfo> getWeeklyBossUidInfo() {
        return this.scene.getPlayers().stream()
            .filter(p ->  p.getDungeonEntryManager().getWeeklyBossDiscountInfo(this.dungeonData) != null)
            .collect(Collectors.toMap(Player::getUid, p -> p.getDungeonEntryManager().getWeeklyBossDiscountInfo(this.dungeonData)));
    }
}
