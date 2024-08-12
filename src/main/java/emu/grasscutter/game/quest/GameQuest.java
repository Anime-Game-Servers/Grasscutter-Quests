package emu.grasscutter.game.quest;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Transient;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.excels.ChapterData;
import emu.grasscutter.data.excels.TriggerExcelConfigData;
import emu.grasscutter.game.dungeons.enums.DungeonPassConditionType;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.ActionReason;
import emu.grasscutter.game.quest.enums.QuestCond;
import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.net.proto.ChapterStateOuterClass;
import emu.grasscutter.net.proto.QuestOuterClass.Quest;

import emu.grasscutter.server.packet.send.PacketChapterStateNotify;
import emu.grasscutter.server.packet.send.PacketDelQuestNotify;
import emu.grasscutter.server.packet.send.PacketQuestListUpdateNotify;
import emu.grasscutter.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.anime_game_servers.core.gi.enums.QuestState;

import javax.annotation.Nullable;
import javax.script.Bindings;
import java.util.HashMap;
import java.util.Map;

@Entity
public class GameQuest {
    @Transient @Getter @Setter private GameMainQuest mainQuest;
    @Transient @Getter private SubQuestData questData;

    @Getter private int subQuestId;
    @Getter private int mainQuestId;
    @Getter @Setter
    public QuestState state;

    @Getter @Setter private int startTime;
    @Getter @Setter private int acceptTime;
    @Getter @Setter private int finishTime;

    @Getter @Setter private long startGameDay;

    @Getter private int[] finishProgressList;
    @Getter private int[] failProgressList;
    @Transient @Getter private Map<String, TriggerExcelConfigData> triggerData;
    @Getter private Map<String, Boolean> triggers;
    private transient Bindings bindings;

    @Deprecated // Morphia only. Do not use.
    public GameQuest() {}

    public GameQuest(GameMainQuest mainQuest, SubQuestData questData) {
        this.mainQuest = mainQuest;
        this.subQuestId = questData.getSubId();
        this.mainQuestId = questData.getMainId();
        this.questData = questData;
        this.state = QuestState.QUEST_STATE_UNSTARTED;
        this.triggerData = new HashMap<>();
        this.triggers = new HashMap<>();
    }

    public void start() {
        clearProgress(false);
        this.acceptTime = Utils.getCurrentSeconds();
        this.startTime = this.acceptTime;
        this.startGameDay = getOwner().getWorld().getTotalGameTimeDays();
        this.state = QuestState.QUEST_STATE_UNFINISHED;
        val triggerCond = questData.getFinishCond().stream()
            .filter(p -> p.getType() == QuestContent.QUEST_CONTENT_TRIGGER_FIRE).toList();
        if (triggerCond.size() > 0) {
            for (val cond : triggerCond) {
                TriggerExcelConfigData newTrigger = GameData.getTriggerExcelConfigDataMap().get(cond.getParam()[0]);
                if (newTrigger != null) {
                    if (this.triggerData == null) {
                        this.triggerData = new HashMap<>();
                    }
                    triggerData.put(newTrigger.getTriggerName(), newTrigger);
                    triggers.put(newTrigger.getTriggerName(), false);
                    val scene = getOwner().getWorld().getSceneById(newTrigger.getSceneId());
                    val group = scene.getScriptManager().getGroupById(newTrigger.getGroupId());
                    getOwner().getWorld().getSceneById(newTrigger.getSceneId()).loadTriggerFromGroup(group, newTrigger.getTriggerName());
                }
            }
        }

        getOwner().sendPacket(new PacketQuestListUpdateNotify(this));

        if (ChapterData.beginQuestChapterMap.containsKey(subQuestId)) {
            getOwner().sendPacket(new PacketChapterStateNotify(
                ChapterData.beginQuestChapterMap.get(subQuestId).getId(),
                ChapterStateOuterClass.ChapterState.CHAPTER_STATE_BEGIN
            ));
        }

        //Some subQuests and talks become active when some other subQuests are unfinished (even from different MainQuests)
        triggerStateEvents();

        getQuestData().getBeginExec().forEach(e -> getOwner().getServer().getQuestSystem().triggerExec(this, e, e.getParam()));
        getOwner().getQuestManager().checkQuestAlreadyFulfilled(this, true);
        getOwner().getDungeonEntryManager().checkQuestForDungeonEntryUpdate(this);

        Grasscutter.getLogger().debug("Quest {} is started", subQuestId);
        save();
    }

    public String getTriggerNameById(int id) {
        TriggerExcelConfigData trigger = GameData.getTriggerExcelConfigDataMap().get(id);
        if (trigger != null) {
            String triggerName = trigger.getTriggerName();
            return triggerName;
        }
        //return empty string if can't find trigger
        return "";
    }

    @Nullable
    public TriggerExcelConfigData getTriggerByName(String name) {
        if(triggerData==null){
            return null;
        }
        return triggerData.get(name);
    }

    public Player getOwner() {
        return this.getMainQuest().getOwner();
    }

    public void setConfig(SubQuestData config) {
        if (config == null || getSubQuestId() != config.getSubId()) return;
        this.questData = config;
    }

    public void setFinishProgress(int index, int value) {
        finishProgressList[index] = value;
    }

    public void setFailProgress(int index, int value) {
        failProgressList[index] = value;
    }

    public boolean clearProgress(boolean notifyDelete){
        //TODO improve
        var oldState = state;
        if (questData.getFinishCond() != null && questData.getFinishCond().size() != 0) {
            this.finishProgressList = new int[questData.getFinishCond().size()];
        }

        if (questData.getFailCond() != null && questData.getFailCond().size() != 0) {
            this.failProgressList = new int[questData.getFailCond().size()];
        }
        setState(QuestState.QUEST_STATE_UNSTARTED);
        finishTime = 0;
        acceptTime = 0;
        startTime = 0;
        if(oldState == QuestState.QUEST_STATE_UNSTARTED){
            return false;
        }
        if(notifyDelete) {
            getOwner().sendPacket(new PacketDelQuestNotify(getSubQuestId()));
        }
        save();
        return true;
    }


    public void finish() {
        finish(false);
    }

    public void finish(boolean isManualFinish) {
        this.state = QuestState.QUEST_STATE_FINISHED;
        this.finishTime = Utils.getCurrentSeconds();

        getOwner().sendPacket(new PacketQuestListUpdateNotify(this));

        if (getQuestData().isFinishParent()) {
            // This quest finishes the questline - the main quest will also save the quest to db, so we don't have to call save() here
            getMainQuest().finish(isManualFinish);
        }

        getQuestData().getFinishExec().forEach(e -> getOwner().getServer().getQuestSystem().triggerExec(this, e, e.getParam()));
        //Some subQuests have conditions that subQuests are finished (even from different MainQuests)
        triggerStateEvents();
        getOwner().getScene().triggerDungeonEvent(DungeonPassConditionType.DUNGEON_COND_FINISH_QUEST, getSubQuestId());

        getOwner().getProgressManager().tryUnlockOpenStates();

        if (ChapterData.endQuestChapterMap.containsKey(subQuestId)) {
            mainQuest.getOwner().sendPacket(new PacketChapterStateNotify(
                ChapterData.endQuestChapterMap.get(subQuestId).getId(),
                ChapterStateOuterClass.ChapterState.CHAPTER_STATE_END
            ));
        }

        val gainItems = questData.getGainItems();
        if(gainItems != null && gainItems.size() > 0){
            gainItems.forEach(item -> {
                getOwner().getInventory().addItem(item.getItemId(), item.getCount(), ActionReason.QuestItem);
            });
        }
        getOwner().getDungeonEntryManager().checkQuestForDungeonEntryUpdate(this);

        save();

        Grasscutter.getLogger().debug("Quest {} is finished", subQuestId);
    }

    //TODO
    public void fail() {
        this.state = QuestState.QUEST_STATE_FAILED;
        this.finishTime = Utils.getCurrentSeconds();

        getOwner().sendPacket(new PacketQuestListUpdateNotify(this));

        //Some subQuests have conditions that subQuests fail (even from different MainQuests)
        triggerStateEvents();
        getQuestData().getFailExec().forEach(e -> getOwner().getServer().getQuestSystem().triggerExec(this, e, e.getParam()));

        if (getQuestData().getTrialAvatarList() != null) {
            getQuestData().getTrialAvatarList().forEach(t -> getOwner().removeTrialAvatarForQuest(t));
        }
        Grasscutter.getLogger().debug("Quest {} is failed", subQuestId);
    }

    /**
     * Triggers events: 'QUEST_COND_STATE_EQUAL', 'QUEST_COND_STATE_NOT_EQUAL',
     * 'QUEST_CONTENT_QUEST_STATE_EQUAL', 'QUEST_CONTENT_QUEST_STATE_NOT_EQUAL'
     */
    public void triggerStateEvents(){
        val questManager = getOwner().getQuestManager();
        val questId = this.subQuestId;
        val state = this.state.getValue();

        questManager.queueEvent(QuestCond.QUEST_COND_STATE_EQUAL, questId, state);
        questManager.queueEvent(QuestCond.QUEST_COND_STATE_NOT_EQUAL, questId, state);
        questManager.queueEvent(QuestContent.QUEST_CONTENT_QUEST_STATE_EQUAL, questId, state);
        questManager.queueEvent(QuestContent.QUEST_CONTENT_QUEST_STATE_NOT_EQUAL, questId, state);

    }

    // Return true if it did the rewind
    public boolean rewind(boolean notifyDelete) {
        //rewind everything after the rewind target
        getMainQuest().getChildQuests().values().stream().filter(p -> p.getQuestData().getOrder() > this.getQuestData().getOrder()).forEach(q -> {
            q.clearProgress(notifyDelete);
        });

        //rewind and restart itself
        clearProgress(notifyDelete);
        this.start();

        return true;
    }

    public void save() {
        getMainQuest().save();
    }

    public Quest toProto() {
        Quest.Builder proto = Quest.newBuilder()
                .setQuestId(getSubQuestId())
                .setState(getState().getValue())
                .setParentQuestId(getMainQuestId())
                .setStartTime(getStartTime())
                .setStartGameTime(438)
                .setAcceptTime(getAcceptTime());

        if (getFinishProgressList() != null) {
            for (int i : getFinishProgressList()) {
                proto.addFinishProgressList(i);
            }
        }

        if (getFailProgressList() != null) {
            for (int i : getFailProgressList()) {
                proto.addFailProgressList(i);
            }
        }

        return proto.build();
    }
}
