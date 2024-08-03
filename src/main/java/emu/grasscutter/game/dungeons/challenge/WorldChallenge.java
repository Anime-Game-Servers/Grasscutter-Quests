package emu.grasscutter.game.dungeons.challenge;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.dungeons.challenge.trigger.*;
import emu.grasscutter.game.dungeons.enums.DungeonPassConditionType;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.entity.EntityMonster;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.props.ElementReactionType;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.server.packet.send.PacketDungeonChallengeBeginNotify;
import emu.grasscutter.server.packet.send.PacketDungeonChallengeFinishNotify;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.anime_game_servers.gi_lua.models.ScriptArgs;
import org.anime_game_servers.gi_lua.models.constants.EventType;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;
import org.anime_game_servers.gi_lua.models.scene.group.SceneTrigger;
import org.anime_game_servers.game_data_models.data.watcher.WatcherTriggerType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * */
@Getter
@RequiredArgsConstructor
public class WorldChallenge {
    private final Scene scene;
    private final SceneGroup group;
    private final ChallengeInfo info; // currentChallengeIndex, currentChallengeId, fatherChallengeIndex
    private final List<Integer> paramList;
    private final Map<Class<? extends ChallengeTrigger>, ChallengeTrigger> challengeTriggers;
    private boolean progress;
    private boolean success;
    @Setter private int startedAt;
    private int finishedTime;

    // Father and child challenge related info
    private final List<WorldChallenge> childChallenge = new ArrayList<>();
    private WorldChallenge fatherChallenge = null;
    private final ChallengeScoreInfo scoreInfo; // success count, fail count

    /**
     * Return if challenge is in progress
     */
    public boolean inProgress(){
        return this.progress;
    }

    /**
     * Get the monster group id spawned by the challenge
     */
    public int getGroupId(){
        return group!=null ? group.getGroupInfo().getId() : 0;
    }


    public int getTimeTaken() {
        return this.finishedTime - this.startedAt;
    }

    private void setFatherChallenge(WorldChallenge fatherChallenge) {
        this.fatherChallenge = fatherChallenge;
    }

    /**
     * Attach a child challenge to this challenge,
     * also set this as father challenge of the child challenge
     * @param childChallenge child challenge to attach
     */
    public void attachChild(WorldChallenge childChallenge) {
        this.childChallenge.add(childChallenge);
        childChallenge.setFatherChallenge(this);

        if (inProgress()) childChallenge.start(); // some child challenges will be added after father challenge started
    }

    public boolean isThisChallenge(int challengeIndex, int challengeId, int groupId) {
        return this.info.getChallengeIndex() == challengeIndex
            && this.info.getChallengeId() == challengeId && getGroupId() == groupId;
    }

    /**
     * Starts the challenge
     */
    public void start(){
        if(inProgress()){
            Grasscutter.getLogger().info("Could not start a in progress challenge.");
            return;
        }
        this.progress = true;
        this.startedAt = getScene().getSceneTimeSeconds();
        this.scene.broadcastPacket(new PacketDungeonChallengeBeginNotify(this));
        this.challengeTriggers.values().forEach(t -> t.onBegin(this));
        this.childChallenge.forEach(WorldChallenge::start); // child challenges could be empty here
    }

    /**
     * Finishes the challenge
     */
    public void done(){
        if(!inProgress()) return;

        finish(true);
        if (this.scene.getDungeonManager() != null && this.scene.getDungeonManager().getDungeonData() != null) {
            this.scene.getPlayers().forEach(p -> p.getActivityManager().triggerWatcher(
                WatcherTriggerType.TRIGGER_FINISH_CHALLENGE,
                String.valueOf(this.scene.getDungeonManager().getDungeonData().getId()),
                String.valueOf(getGroupId()),
                String.valueOf(this.info.getChallengeId())
            ));
        }
        // might be different for other challenge types, so far only found to use in Tower challenge
        final int remainingTime = Optional.ofNullable(this.challengeTriggers.get(TimeTrigger.class))
            .map(ChallengeTrigger::getGoal).map(AtomicInteger::get)
            .map(goalTime -> goalTime - getTimeTaken()).orElse(getFinishedTime());

        this.scene.getScriptManager().callEvent(new ScriptArgs(getGroupId(), EventType.EVENT_CHALLENGE_SUCCESS)
                .setParam2(remainingTime).setEventSource(Integer.toString(this.info.getChallengeIndex())));

        this.scene.triggerDungeonEvent(
            DungeonPassConditionType.DUNGEON_COND_FINISH_CHALLENGE, this.info.getChallengeId(), this.info.getChallengeIndex());
    }

    /**
     * Fails the challenge
     */
    public boolean fail(){
        if(!inProgress()) return false;

        finish(false);
        this.scene.getScriptManager().callEvent(new ScriptArgs(getGroupId(), EventType.EVENT_CHALLENGE_FAIL)
            .setEventSource(Integer.toString(this.info.challengeIndex())));
        this.childChallenge.forEach(WorldChallenge::fail); // child challenges could be empty here
        return true;
    }

    /**
     * Set challenge properties as it finishes
     * @param success true = challenge success, false = challenge fails
     */
    protected void finish(boolean success){
        this.progress = false;
        this.success = success;
        this.finishedTime = this.scene.getSceneTimeSeconds() - this.startedAt;
        this.scene.broadcastPacket(new PacketDungeonChallengeFinishNotify(this));
        Optional.ofNullable(this.fatherChallenge).ifPresent(fc ->
            fc.onIncFailSuccScore(success, getScoreInfo().get(success)));
    }

    /**
     * Constantly checking if challenge has timed out
     */
    public void onCheckTimeOut(){
        if(!inProgress()) return;

        Optional.ofNullable(this.challengeTriggers.get(TimeTrigger.class)).ifPresent(t -> t.onCheckTimeout(this));
        this.childChallenge.stream().filter(WorldChallenge::inProgress).forEach(cc ->
            Optional.ofNullable(cc.getChallengeTriggers().get(TimeTrigger.class)).ifPresent(t -> t.onCheckTimeout(cc)));
    }

    /**
     * Invoke when player killed a monster
     * @param monster Monster that belongs to the group spawned by the challenge
     */
    public void onMonsterDeath(EntityMonster monster){
        if(!inProgress() || monster.getGroupId() != getGroupId())
            return;


        Optional.ofNullable(this.challengeTriggers.get(KillMonsterTrigger.class))
            .ifPresent(t -> t.onMonsterDeath(this, monster));
        this.childChallenge.stream().filter(WorldChallenge::inProgress).forEach(cc ->
            Optional.ofNullable(cc.getChallengeTriggers().get(KillMonsterTrigger.class))
                .ifPresent(t -> t.onMonsterDeath(cc, monster)));
    }

    public void onGroupTriggerDeath(SceneTrigger trigger){
        if(!inProgress()) return;
        if(trigger.getGroupId() != getGroupId()) return;

        Optional.ofNullable(this.challengeTriggers.get(TriggerGroupTriggerTrigger.class))
            .ifPresent(t -> t.onGroupTrigger(this, trigger));
        this.childChallenge.stream().filter(WorldChallenge::inProgress).forEach(cc ->
            Optional.ofNullable(cc.getChallengeTriggers().get(TriggerGroupTriggerTrigger.class))
                .ifPresent(t -> t.onGroupTrigger(cc, trigger)));
    }

    public void onGadgetDamage(EntityGadget gadget){
        if(!inProgress() || gadget.getGroupId() != getGroupId()) return;

        Optional.ofNullable(this.challengeTriggers.get(GuardTrigger.class))
            .ifPresent(t -> t.onGadgetDamage(this, gadget));
        this.childChallenge.stream().filter(WorldChallenge::inProgress).forEach(cc ->
            Optional.ofNullable(cc.getChallengeTriggers().get(GuardTrigger.class))
                .ifPresent(t -> t.onGadgetDamage(cc, gadget)));
    }

    /**
     * Invoke when player triggered elemental reaction
     * @param defender target that element reaction invoked on
     * @param reactionType Reaction triggered by attacker
     */
    public void onElementReaction(GameEntity defender, ElementReactionType reactionType){
        if (!inProgress()) return;
        if (defender instanceof EntityMonster monster && monster.getGroupId() != getGroupId()) return;

        Optional.ofNullable(this.challengeTriggers.get(ElementReactionTrigger.class))
            .ifPresent(t -> t.onElementReaction(this, defender, reactionType));
        this.childChallenge.stream().filter(WorldChallenge::inProgress).forEach(cc ->
            Optional.ofNullable(cc.getChallengeTriggers().get(ElementReactionTrigger.class))
                .ifPresent(t -> t.onElementReaction(cc, defender, reactionType)));
    }

    /**
     * Invoke when player damaging monster or monster damaging player's shield
     * @param entity Monster that belongs to the group spawned by the challenge
     * @param damage Damage dealt
     */
    public void onDamageMonsterOrShield(GameEntity entity, float damage) {
        if(!inProgress()) return;
        if (entity instanceof EntityMonster monster && monster.getGroupId() != getGroupId()) return;

        Optional.ofNullable(this.challengeTriggers.get(DamageCountTrigger.class))
            .ifPresent(t -> t.onDamageMonsterOrShield(this, damage));
        this.childChallenge.stream().filter(WorldChallenge::inProgress).forEach(cc ->
            Optional.ofNullable(cc.getChallengeTriggers().get(DamageCountTrigger.class))
                .ifPresent(t -> t.onDamageMonsterOrShield(cc, damage)));
    }

    /**
     * Invoke when CHILD challenge finishes or fails
     */
    public void onIncFailSuccScore(boolean useSucc, int score) {
        if(!inProgress()) return;

        Optional.ofNullable(this.challengeTriggers.get(FatherTrigger.class))
            .ifPresent(t -> t.onIncFailSuccScore(this, useSucc, score));
    }

    public void resetTimer(int incrementCount) {
        val timerTrigger =  Optional.ofNullable(this.challengeTriggers.get(TimeTrigger.class));
        if (timerTrigger.isEmpty()) return;

        if (incrementCount > 0) {
            // reset time count partially
            timerTrigger.get().getGoal().addAndGet(incrementCount);
        } else {
            // reset time count
            this.startedAt = getScene().getSceneTimeSeconds();
        }

        timerTrigger.get().onBegin(this);
    }

    public void resetFreezeTime() {
        val reactionTrigger = Optional.ofNullable(this.challengeTriggers.get(ElementReactionTrigger.class));
        if (reactionTrigger.isEmpty()) return;

        reactionTrigger.get().getScore().set(0);
        reactionTrigger.get().onBegin(this);
        this.startedAt = getScene().getSceneTimeSeconds();
    }
}
