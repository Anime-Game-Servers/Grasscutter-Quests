package emu.grasscutter.game.quest;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.Transient;
import emu.grasscutter.Loggers;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.ScriptSceneData;
import emu.grasscutter.data.common.quest.MainQuestData;
import emu.grasscutter.data.common.quest.MainQuestData.TalkData;
import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.excels.RewardData;
import emu.grasscutter.database.DatabaseHelper;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.ActionReason;
import emu.grasscutter.game.quest.enums.ParentQuestState;
import emu.grasscutter.game.quest.enums.QuestCond;
import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.game.world.World;
import emu.grasscutter.server.packet.send.PacketCodexDataUpdateNotify;
import emu.grasscutter.server.packet.send.PacketFinishedParentQuestUpdateNotify;
import emu.grasscutter.server.packet.send.PacketQuestUpdateQuestVarNotify;
import emu.grasscutter.utils.Position;
import emu.grasscutter.utils.Utils;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.core.gi.enums.QuestState;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.anime_game_servers.multi_proto.gi.messages.quest.parent.ParentQuest;
import org.anime_game_servers.multi_proto.gi.messages.quest.parent.ChildQuest;

import javax.annotation.Nullable;
import java.util.*;

@Entity(value = "quests", useDiscriminator = false)
public class GameMainQuest {
    public static final Logger logger = Loggers.getQuestSystem();
    @Id private ObjectId id;
    @Indexed @Getter private int ownerUid;
    @Transient @Getter private Player owner;
    @Transient @Getter private QuestManager questManager;
    @Getter private Map<Integer, GameQuest> childQuests;
    @Getter private int parentQuestId;
    @Getter private int[] questVars;
    @Getter private long[] timeVar;
    //QuestUpdateQuestVarReq is sent in two stages...
    private List<Integer> questVarsUpdate;
    @Getter private ParentQuestState state;
    @Getter private boolean isFinished;
    @Getter List<QuestGroupSuite> questGroupSuites;

    @Getter int[] suggestTrackMainQuestList;
    @Getter private Map<Integer,TalkData> talks;

    @Deprecated // Morphia only. Do not use.
    public GameMainQuest() {}

    public GameMainQuest(Player player, int parentQuestId) {
        this.owner = player;
        this.ownerUid = player.getUid();
        this.questManager = player.getQuestManager();
        this.parentQuestId = parentQuestId;
        this.childQuests = new HashMap<>();
        this.talks = new HashMap<>();
        //official server always has a list of 5 questVars, with default value 0
        this.questVars = new int[] {0,0,0,0,0};
        this.timeVar = new long[] {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}; // theoretically max is 10 here
        this.state = ParentQuestState.PARENT_QUEST_STATE_NONE;
        this.questGroupSuites = new ArrayList<>();
        addAllChildQuests();
    }

    public List<Integer> getQuestVarsUpdate() {
        if(questVarsUpdate == null){
            questVarsUpdate = new ArrayList<>();
        }
        return questVarsUpdate;
    }

    private void addAllChildQuests() {
        val mainQuestData = getMainQuestData();
        if(mainQuestData== null) {
            logger.error("mainQuestData is null for parentQuestId {}", parentQuestId);
            return;
        }
        val subQuests = Arrays.stream(mainQuestData.getSubQuests()).toList();
        for (SubQuestData subQuestData : subQuests) {
            this.childQuests.put(subQuestData.getSubId(), new GameQuest(this, subQuestData));
        }
    }

    public Collection<GameQuest> getUnfinishedQuests(){
        return childQuests.values().stream()
            .filter(q->q.getState().getValue() == QuestState.QUEST_STATE_UNFINISHED.getValue())
            .toList();
    }

    public void setOwner(Player player) {
        if (player.getUid() != this.getOwnerUid()) return;
        this.owner = player;
    }

    public int getQuestVar(int i) {
        return questVars[i];
    }
    public void setQuestVar(int i, int value) {
        int previousValue = this.questVars[i];
        this.questVars[i] = value;

        logger.debug("questVar {} value changed from {} to {}", i, previousValue, value);
        triggerQuestVarAction(i, this.questVars[i]);
    }

    public void incQuestVar(int i, int inc) {
        int previousValue = this.questVars[i];
        this.questVars[i] += inc;

        logger.debug("questVar {} value incremented from {} to {}", i, previousValue, previousValue + inc);
        triggerQuestVarAction(i, this.questVars[i]);
    }

    public void decQuestVar(int i, int dec) {
        int previousValue = this.questVars[i];
        this.questVars[i] -= dec;

        logger.debug("questVar {} value decremented from {} to {}", i, previousValue, previousValue - dec);
        triggerQuestVarAction(i, this.questVars[i]);
    }

    public void setRandomQuestVar(int i, int low, int high){
        int previousValue = this.questVars[i];
        this.questVars[i] = Utils.random.nextInt(low, high);

        logger.debug("questVar {} value random changed from {} to {}", i, previousValue, this.questVars[i]);
        triggerQuestVarAction(i, this.questVars[i]);
    }

    private void triggerQuestVarAction(int index, int value) {
        var questManager = this.getQuestManager();
        questManager.queueEvent(QuestCond.QUEST_COND_QUEST_VAR_EQUAL, index, value);
        questManager.queueEvent(QuestCond.QUEST_COND_QUEST_VAR_GREATER, index, value);
        questManager.queueEvent(QuestCond.QUEST_COND_QUEST_VAR_LESS, index, value);
        questManager.queueEvent(QuestContent.QUEST_CONTENT_QUEST_VAR_EQUAL, index, value);
        questManager.queueEvent(QuestContent.QUEST_CONTENT_QUEST_VAR_GREATER, index, value);
        questManager.queueEvent(QuestContent.QUEST_CONTENT_QUEST_VAR_LESS, index, value);
        this.getOwner()
            .sendPacket(new PacketQuestUpdateQuestVarNotify(this.getParentQuestId(), this.questVars));
    }


    public GameQuest getChildQuestById(int id) {
        return this.getChildQuests().get(id);
    }
    public GameQuest getChildQuestByOrder(int order) {
        return this.getChildQuests().values().stream().filter(p -> p.getQuestData().getOrder() == order).toList().get(0);
    }

    //TODO maybe just store it in the GameMainQuest object?
    @Nullable
    public MainQuestData getMainQuestData() {
        return GameData.getMainQuestDataMap().get(this.parentQuestId);
    }

    public void finish(boolean isManualFinish) {
        // Avoid recursion from child finish() in GameQuest
        // when auto finishing all child quests with QUEST_STATE_UNFINISHED (below)
        if (this.isFinished) {
            logger.debug("Skip main quest finishing because it's already finished");
            return;
        }

        this.isFinished = true;
        this.state = ParentQuestState.PARENT_QUEST_STATE_FINISHED;

         /*
            On force finish we also need to check for unfinished childQuests in this MainQuest
            force them to complete and send a packet about this to the user,
            because at some points there are special "invisible" child quests that control
            some situations.

            For example, subQuest 35312 is responsible for the event of leaving the territory
            of the island with a statue and automatically returns the character back,
            quest 35311 completes the main quest line 353 and starts 35501 from
            new MainQuest 355 but if 35312 is not completed after the completion
            of the main quest 353 - the character will not be able to leave place
            (return again and again)
            TODO don't finish quests with reroll exec in finish
            */
        if(isManualFinish) {
            this
                .getChildQuests()
                .values()
                .stream()
                .filter(p -> p.state != QuestState.QUEST_STATE_FINISHED)
                .forEach(GameQuest::finish);
        }

        this.getOwner().getSession().send(new PacketFinishedParentQuestUpdateNotify(this));
        this.getOwner().getSession().send(new PacketCodexDataUpdateNotify(this));

        this.save();

        // Add rewards
        val mainQuestData = getMainQuestData();
        if(mainQuestData!= null && mainQuestData.getRewardIdList()!=null) {
            for (int rewardId : mainQuestData.getRewardIdList()) {
                RewardData rewardData = GameData.getRewardDataMap().get(rewardId);

                if (rewardData == null) {
                    continue;
                }

                getOwner().getInventory().addItemParamDatas(rewardData.getRewardItemList(), ActionReason.QuestReward);
            }
        }

        // handoff main quest
        /*if (mainQuestData.getSuggestTrackMainQuestList() != null) {
            Arrays.stream(mainQuestData.getSuggestTrackMainQuestList())
                .forEach(getQuestManager()::startMainQuest);
        }*/
    }
    //TODO
    public void fail() {}
    public void cancel() {}


    public List<Position> rewindTo(GameQuest targetQuest, boolean notifyDelete){
        if(targetQuest == null || !targetQuest.rewind(notifyDelete)){
            return null;
        }

        /*if(rewindPositions.isEmpty()){
            addRewindPoints();
        }*/

        List<Position> posAndRot = new ArrayList<>();
        if(hasRewindPosition(targetQuest.getSubQuestId(), posAndRot)){
            return posAndRot;
        }

        List<GameQuest> rewindQuests = getChildQuests().values().stream()
            .filter(p -> (p.getState() == QuestState.QUEST_STATE_UNFINISHED || p.getState() == QuestState.QUEST_STATE_FINISHED) &&
                p.getQuestData() != null && p.getQuestData().isRewind()).toList();

        for (GameQuest quest : rewindQuests) {
            if (hasRewindPosition(quest.getSubQuestId(), posAndRot)) {
                return posAndRot;
            }

        }
        return null;
    }

    public GameQuest getRewindTarget(){
        var activeQuests = getChildQuests().values().stream()
            .filter(p -> (p.getState() == QuestState.QUEST_STATE_UNFINISHED || p.getState() == QuestState.QUEST_STATE_FINISHED)).toList();
        var highestActiveQuest = activeQuests.stream()
            .filter(q -> q.getQuestData() != null)
            .max(Comparator.comparing(q -> q.getQuestData().getOrder()))
            .orElse(null);

        if (highestActiveQuest == null) {
            var firstUnstarted = getChildQuests().values().stream()
                .filter(q -> q.getQuestData() != null && q.getState().getValue() != QuestState.QUEST_STATE_FINISHED.getValue())
                .min(Comparator.comparingInt(a -> a.getQuestData().getOrder()));
            if(firstUnstarted.isEmpty()){
                // all quests are probably finished, do don't rewind and maybe also set the mainquest to finished?
                return null;
            }
            highestActiveQuest = firstUnstarted.get();
            //todo maybe try to accept quests if there is no active quest and no rewind target?
            //tryAcceptSubQuests(QuestTrigger.QUEST_COND_NONE, "", 0);
        }

        var highestOrder = highestActiveQuest.getQuestData().getOrder();
        var rewindTarget = getChildQuests().values().stream()
            .filter(q -> q.getQuestData() != null)
            .filter(q -> q.getQuestData().isRewind() && q.getQuestData().getOrder() <= highestOrder)
            .max(Comparator.comparingInt(a -> a.getQuestData().getOrder()))
            .orElse(highestActiveQuest);

        return rewindTarget;
    }

    // Rewinds to the last finished/unfinished rewind quest, and returns the avatar rewind position (if it exists)
    public List<Position> rewind() {
        if (this.questManager == null) {
            this.questManager = getOwner().getQuestManager();
        }
        val rewindTarget = getRewindTarget();
        if(rewindTarget == null) return null;
        return rewindTo(rewindTarget, false);
    }

    public boolean hasRewindPosition(int subId, List<Position> posAndRot){
        val questRewind = GameData.getRewindDataMap().get(subId);
        if (questRewind == null) return false;

        val avatarData = questRewind.getAvatar();
        if (avatarData == null) return false;

        String avatarPos = avatarData.getPos();
        val guide = GameData.getQuestDataMap().get(subId).getGuide();
        if (guide == null) return false;

        int sceneId = guide.getGuideScene();
        ScriptSceneData fullGlobals = GameData.getScriptSceneDataMap().get("flat.luas.scenes.full_globals.lua.json");
        if (fullGlobals == null) return false;

        ScriptSceneData.ScriptObject dummyPointScript = fullGlobals.getScriptObjectList().get(sceneId + "/scene" + sceneId + "_dummy_points.lua");
        if (dummyPointScript == null) return false;

        Map<String, List<Float>> dummyPointMap = dummyPointScript.getDummyPoints();
        if (dummyPointMap == null) return false;

        List<Float> avatarPosPos = dummyPointMap.get(avatarPos + ".pos");
        List<Float> avatarPosRot = dummyPointMap.get(avatarPos + ".rot");
        if (avatarPosPos == null) return false;

        posAndRot.add(0, new Position(avatarPosPos.get(0),avatarPosPos.get(1),avatarPosPos.get(2))); // position
        posAndRot.add(1, new Position(avatarPosRot.get(0),avatarPosRot.get(1),avatarPosRot.get(2))); //rotation
        logger.info("Succesfully loaded rewind data for subQuest {}", subId);
        return true;
    }

    public boolean hasTeleportPostion(int subId, List<Position> posAndRot){
        val questTransmit = GameData.getTeleportDataMap().get(subId);
        if (questTransmit == null) return false;

        val transmitPoint = questTransmit.getTransmitPoints().size() > 0 ? questTransmit.getTransmitPoints().get(0) : null;
        if (transmitPoint == null) return false;

        String transmitPos = transmitPoint.getPos();
        int sceneId = transmitPoint.getSceneId();
        ScriptSceneData fullGlobals = GameData.getScriptSceneDataMap().get("flat.luas.scenes.full_globals.lua.json");
        if (fullGlobals == null) return false;

        ScriptSceneData.ScriptObject dummyPointScript = fullGlobals.getScriptObjectList().get(sceneId + "/scene" + sceneId + "_dummy_points.lua");
        if (dummyPointScript == null) return false;

        Map<String, List<Float>> dummyPointMap = dummyPointScript.getDummyPoints();
        if (dummyPointMap == null) return false;

        List<Float> transmitPosPos = dummyPointMap.get(transmitPos + ".pos");
        List<Float> transmitPosRot = dummyPointMap.get(transmitPos + ".rot");
        if (transmitPosPos == null) return false;

        posAndRot.add(0, new Position(transmitPosPos.get(0), transmitPosPos.get(1), transmitPosPos.get(2))); // position
        posAndRot.add(1, new Position(transmitPosRot.get(0), transmitPosRot.get(1), transmitPosRot.get(2))); // rotation
        logger.info("Succesfully loaded teleport data for subQuest {}", subId);
        return true;
    }

    public void checkProgress(){
        for (var quest : getChildQuests().values()){
            if(quest.getState() == QuestState.QUEST_STATE_UNFINISHED) {
                questManager.checkQuestAlreadyFullfilled(quest);
            }
        }
    }

    public void tryFailSubQuests(QuestContent condType, String paramStr, int... params) {
        try {
            List<GameQuest> subQuestsWithCond = getChildQuests().values().stream()
                .filter(p -> p.getState() == QuestState.QUEST_STATE_UNFINISHED)
                .filter(p -> p.getQuestData().getFailCond().stream().anyMatch(q -> q.getType() == condType))
                .toList();
            val questSystem = this.getOwner().getServer().getQuestSystem();

            for (GameQuest subQuestWithCond : subQuestsWithCond) {
                val failCond = subQuestWithCond.getQuestData().getFailCond();
                val failCondComb = subQuestWithCond.getQuestData().getFailCondComb();
                val failProgressList = subQuestWithCond.getFailProgressList();

                val shouldFail = questSystem.checkAndUpdateContent(
                    subQuestWithCond, failProgressList, failCond, failCondComb,
                    condType, paramStr, params);

                if (shouldFail)
                    subQuestWithCond.fail();
            }

        } catch (Exception e) {
            logger.error("An error occurred while trying to fail quest.", e);
        }
    }

    public void tryFinishSubQuests(QuestContent condType, String paramStr, int... params) {
        try {
            List<GameQuest> subQuestsWithCond = getChildQuests().values().stream()
                //There are subQuests with no acceptCond, but can be finished (example: 35104)
                .filter(p -> p.getState() == QuestState.QUEST_STATE_UNFINISHED && p.getQuestData().getAcceptCond() != null)
                .filter(p -> p.getQuestData().getFinishCond().stream().anyMatch(q -> q.getType() == condType))
                .toList();
            val questSystem = this.getOwner().getServer().getQuestSystem();

            for (GameQuest subQuestWithCond : subQuestsWithCond) {
                val finishCond = subQuestWithCond.getQuestData().getFinishCond();
                val finishCondComb = subQuestWithCond.getQuestData().getFinishCondComb();
                val finishProgressList = subQuestWithCond.getFinishProgressList();

                val shouldFinish = questSystem.checkAndUpdateContent(subQuestWithCond,
                    finishProgressList, finishCond, finishCondComb,
                    condType, paramStr, params);

                if (shouldFinish)
                    subQuestWithCond.finish();
            }
        } catch (Exception e) {
            logger.debug("An error occurred while trying to finish quest.", e);
        }
    }

    public void save() {
        DatabaseHelper.saveQuest(this);
    }

    public void delete() {
        DatabaseHelper.deleteQuest(this);
    }

    public ParentQuest toProto(boolean withChildQuests) {
        ParentQuest proto = new ParentQuest();
        proto.setParentQuestId(getParentQuestId());
        proto.setFinished(isFinished());
        proto.setParentQuestState(getState().getValue());
        proto.setVideoKey(QuestManager.getQuestKey(parentQuestId));


        if(withChildQuests) {
            proto.setChildQuestList(this.getChildQuests().values().stream()
                .filter(quest->quest.getState() != QuestState.QUEST_STATE_UNSTARTED)
                .map(quest->new ChildQuest(quest.getSubQuestId(), quest.getState().getValue()))
                .toList());
        }

        proto.setQuestVar(Arrays.stream(getQuestVars()).boxed().toList());
        return proto;
    }

    // TimeVar handling TODO check if ingame or irl time
    public boolean initTimeVar(int index){
        if(index >= this.timeVar.length){
            logger.error("Trying to init out of bounds time var {} for quest {}", index, this.parentQuestId);
            return false;
        }
        this.timeVar[index] = owner.getWorld().getTotalGameTimeMinutes();
        owner.getActiveQuestTimers().add(this.parentQuestId);
        return true;
    }

    public boolean clearTimeVar(int index){
        if(index >= this.timeVar.length){
            logger.error("Trying to clear out of bounds time var {} for quest {}", index, this.parentQuestId);
            return false;
        }
        this.timeVar[index] = -1;
        if(!checkActiveTimers()){
            owner.getActiveQuestTimers().remove(this.parentQuestId);
        }
        return true;
    }

    public boolean checkActiveTimers(){
        return Arrays.stream(timeVar).anyMatch(value -> value!=-1);
    }

    public long getDaysSinceTimeVar(int index){
        if(index >= this.timeVar.length){
            logger.error("Trying to get days for out of bounds time var {} for quest {}", index, this.parentQuestId);
            return -1;
        }
        val varTime = timeVar[index];

        if(varTime == -1){
            return 0;
        }

        return owner.getWorld().getTotalGameTimeDays() - World.getDaysForGameTime(varTime);
    }

    public long getHoursSinceTimeVar(int index){
        if(index >= this.timeVar.length){
            logger.error("Trying to get hours for out of bounds time var {} for quest {}", index, this.parentQuestId);
            return -1;
        }
        val varTime = timeVar[index];

        if(varTime == -1){
            return 0;
        }

        return owner.getWorld().getTotalGameTimeDays() - World.getHoursForGameTime(varTime);
    }
}
