package emu.grasscutter.game.player;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Transient;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.CoopChapterData;
import emu.grasscutter.server.packet.send.PacketCoopChapterUpdateNotify;
import emu.grasscutter.server.packet.send.PacketCoopPointUpdateNotify;
import emu.grasscutter.server.packet.send.PacketCoopProgressUpdateNotify;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import messages.coop.*;
import org.anime_game_servers.core.gi.enums.QuestState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class CoopHandler {
    @Transient private Player player;
    @Getter private Map<Integer, CoopCardEntry> coopCards;
    @Getter @Setter private int curCoopPoint;

    public CoopHandler(Player player) {
        this.player = player;
        this.coopCards = new Int2ObjectOpenHashMap<>();
        this.curCoopPoint = 0;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void unlockChapterUpdateNotify(int chapterId) {
        val coopCard = this.coopCards.get(chapterId);

        //find first point (POINT_START)
        val startPointId = GameData.getCoopPointDataMap().values().stream()
                .filter(j -> j.getChapterId() == chapterId && j.getPointPosId() == 1)
                .toList()
                .get(0)
                .getId();

        //save to database
        //set first point to selfConfidence 5
        coopCard.getPoints()
                .get(startPointId)
                .setSelfConfidence(5);
        //set first point to STATE_STARTED
        coopCard.getPoints()
                .get(startPointId)
                .setState(CoopPointState.STATE_STARTED);
        //set card as accepted
        coopCard.setAccepted(true);

        //build proto
        val coopChapter = new CoopChapter();
        coopChapter.setId(chapterId);
        coopChapter.setState(CoopChapterState.STATE_ACCEPT);
        coopChapter.setTotalEndCount(coopCard.getTotalEndCount());
        //coopCgList
        val coopCgList = new ArrayList<CoopCg>();
        GameData.getCoopCGDataMap().values().stream()
                .filter(x -> x.getChapterId() == chapterId)
                .forEach(x -> coopCgList.add(new CoopCg(x.getId())));
        coopChapter.setCoopCgList(coopCgList);
        //coopPointList
        val coopPointList = new ArrayList<CoopPoint>();
        GameData.getCoopPointDataMap().values().stream()
                .filter(x -> x.getChapterId() == chapterId)
                .forEach(x -> {
                    val coopPoint = new CoopPoint(x.getId());
                    //set first point
                    if (startPointId == x.getId()) {
                        coopPoint.setSelfConfidence(5);
                        coopPoint.setState(CoopPointState.STATE_STARTED);
                    }
                    coopPointList.add(coopPoint);
                });
        coopChapter.setCoopPointList(coopPointList);
        //coopRewardList
        val coopRewardList = new ArrayList<CoopReward>();
        GameData.getCoopRewardDataMap().values().stream()
                .filter(x -> x.getChapterId() == chapterId)
                .forEach(x -> coopRewardList.add(new CoopReward(x.getId(), CoopRewardState.STATE_LOCK)));
        coopChapter.setCoopRewardList(coopRewardList);

        //send packet
        this.player.sendPacket(new PacketCoopChapterUpdateNotify(List.of(coopChapter)));
    }

    //todo: modifying finishedEndCount needs to call this with "COOP_COND_CHAPTER_END_ALL_FINISH"
    public void conditionMetChapterUpdateNotify(int arg, String condType) {
        //get a list of everything we need to update
        val updateList = GameData.getCoopChapterDataMap().values().stream().filter(x -> !x.getUnlockCond().stream().filter(y -> y.getArgs()[0] == arg && y.getType().equals(condType)).toList().isEmpty());
        val coopChapterList = new ArrayList<CoopChapter>();
        updateList.forEach(chapter -> {
            coopChapterList.add(initializeCoopChapter(chapter));
        });

        //send packet
        if (coopChapterList.isEmpty()) return;
        this.player.sendPacket(new PacketCoopChapterUpdateNotify(coopChapterList));
    }

    public List<CoopChapter> getFullCoopDataList() {
        val chapterList = new ArrayList<CoopChapter>();

        GameData.getCoopChapterDataMap().values().forEach(chapter -> {
            val coopChapter = initializeCoopChapter(chapter);
            val coopCardEntry = this.coopCards.computeIfAbsent(chapter.getId(), v -> new CoopCardEntry(chapter.getId()));

            //points
            List<CoopPoint> pointList = new ArrayList<>();
            coopCardEntry.getPoints().entrySet().stream()
                    .filter(e -> e.getValue().getState() != CoopPointState.STATE_UNSTARTED)
                    .forEach(e -> {
                        val point = new CoopPoint();
                        point.setId(e.getKey());
                        point.setState(e.getValue().getState());
                        pointList.add(point);
                    });
            coopChapter.setCoopPointList(pointList);

            chapterList.add(coopChapter);
        });

        return chapterList;
    }

    public CoopChapter initializeCoopChapter(CoopChapterData chapter) {
        val coopChapter = new CoopChapter();

        //id
        coopChapter.setId(chapter.getId());

        //state, lockReasonList
        val coopCardEntry = this.coopCards.computeIfAbsent(chapter.getId(), v -> new CoopCardEntry(chapter.getId()));
        if (coopCardEntry.getAccepted()) {
            coopChapter.setState(CoopChapterState.STATE_ACCEPT);
        } else {
            List<Integer> lockReasonList = this.getLockReasonList(chapter);
            if (lockReasonList.isEmpty()) {
                coopChapter.setState(CoopChapterState.STATE_COND_MEET);
            } else {
                coopChapter.setState(CoopChapterState.STATE_COND_NOT_MEET);
                coopChapter.setLockReasonList(lockReasonList);
            }
        }

        //totalEndCount
        coopChapter.setTotalEndCount(coopCardEntry.getTotalEndCount());

        return coopChapter;
    }

    private List<Integer> getLockReasonList(CoopChapterData chapter) {
        val lockReasonList = new ArrayList<Integer>();

        for (int i = 0; i < chapter.getUnlockCond().size(); ++i) {
            val condition = chapter.getUnlockCond().get(i);
            val arg = condition.getArgs()[0];
            switch (condition.getType()) {
                case "COOP_COND_FINISH_QUEST" -> {
                    val quest = this.player.getQuestManager().getQuestById(arg);
                    if (quest == null || !quest.getState().equals(QuestState.QUEST_STATE_FINISHED))
                        lockReasonList.add(i + 1);
                }
                case "COOP_COND_PLAYER_LEVEL" -> {
                    if (this.player.getLevel() < arg)
                        lockReasonList.add(i + 1);
                }
                case "COOP_COND_CHAPTER_END_ALL_FINISH" -> {
                    val card = this.coopCards.get(arg);
                    if (card.getFinishedEndCount() != card.getTotalEndCount())
                        lockReasonList.add(i + 1);
                }
                default -> {
                    Grasscutter.getLogger().warn("Unknown Coop condition type {} in coop chapter {}", condition.getType(), chapter.getId());
                    lockReasonList.add(i + 1);
                }
            }

        }
        return lockReasonList;
    }

    public void checkNextCoopPointAccept(int questId) {
        val coopPointCanidateList = GameData.getCoopPointDataMap().values().stream().filter(x -> x.getAcceptQuest() == questId).toList();
        if (coopPointCanidateList.isEmpty()) return;
        //there's always only one
        val coopPointData = coopPointCanidateList.get(0);

        //finish previous coopPoint if it exists
        if (this.curCoopPoint != 0) {
            val curCoopPointCanidateList = GameData.getCoopPointDataMap().values().stream().filter(x -> x.getId() == this.curCoopPoint).toList();
            if (!curCoopPointCanidateList.isEmpty()) {
                val curSelfConfidence = this.coopCards.get(curCoopPointCanidateList.get(0).getChapterId()).getMainCoop().selfConfidence;
                //save and send packet
                updateCoopPoint(curCoopPoint, curSelfConfidence, CoopPointState.STATE_FINISHED, true);
            }
        }

        //new current coop point
        this.curCoopPoint = coopPointData.getId();

        //start current coop point and notify if it isn't already started (from a UnlockCoopChapterReq)
        val curChapter = this.coopCards.get(coopPointData.getChapterId());
        boolean shouldNotify = curChapter.getPoints().get(coopPointData.getId()).getState() != CoopPointState.STATE_STARTED;
        //save and it may send packet
        updateCoopPoint(coopPointData.getId(), curChapter.getMainCoop().selfConfidence, CoopPointState.STATE_FINISHED, shouldNotify);

        this.player.sendPacket(new PacketCoopProgressUpdateNotify(coopPointData.getId(), true));
    }

    public void updateCoopPoint(int coopPointId, int selfConfidence, CoopPointState state, boolean shouldNotify) {
        val coopPointCanidateList = GameData.getCoopPointDataMap().values().stream().filter(x -> x.getId() == coopPointId).toList();
        if (coopPointCanidateList.isEmpty()) return;
        val coopPointData = coopPointCanidateList.get(0);

        //save
        val coopPoint = this.coopCards.get(coopPointData.getChapterId()).getPoints().get(coopPointId);
        coopPoint.selfConfidence = selfConfidence;
        coopPoint.state = state;

        //send packet
        if (shouldNotify) {
            player.sendPacket(new PacketCoopPointUpdateNotify(new CoopPoint(coopPointId, selfConfidence, state)));
        }
    }


    @Entity
    public static class CoopCardEntry {
        @Getter @Setter private Boolean accepted;
        @Getter @Setter private Boolean viewed;
        @Getter private int totalEndCount;
        @Getter @Setter private int finishedEndCount;
        @Getter private Map<Integer, CoopPointEntry> points;
        @Getter private Map<Integer, CoopCGEntry> cgs;
        @Getter private Map<Integer, CoopRewardEntry> rewards;
        @Getter @Setter private MainCoopData mainCoop;
        @Getter @Setter private int curCoopPoint;

        public CoopCardEntry(int chapterId) {
            this.accepted = false;
            this.viewed = false;
            //CGs
            this.cgs = new HashMap<>();
            GameData.getCoopCGDataMap().values().stream()
                    .filter(j -> j.getChapterId() == chapterId)
                    .forEach(j -> this.cgs.put(j.getId(), new CoopCGEntry()));
            //points
            val coopPoints = GameData.getCoopPointDataMap().values().stream()
                    .filter(j -> j.getChapterId() == chapterId)
                    .toList();
            this.totalEndCount = (int) coopPoints.stream()
                    .filter(j -> j.getType().equals("POINT_END"))
                    .count();
            this.points = new HashMap<>();
            coopPoints.forEach(j -> this.points.put(j.getId(), new CoopPointEntry()));
            //Rewards
            this.rewards = new HashMap<>();
            GameData.getCoopRewardDataMap().values().stream()
                    .filter(j -> j.getChapterId() == chapterId)
                    .forEach(j -> this.rewards.put(j.getId(), new CoopRewardEntry()));
            this.finishedEndCount = 0;
            this.mainCoop = new MainCoopData();
        }

        @Entity
        public static class CoopCGEntry {
            private CoopCGEntry() {
            }
        }

        @Entity
        public static class CoopPointEntry {
            @Getter @Setter private int selfConfidence;
            @Getter @Setter private CoopPointState state;

            private CoopPointEntry() {
                this.selfConfidence = 0;
                this.state = CoopPointState.STATE_UNSTARTED;
            }
        }

        @Entity
        public static class CoopRewardEntry {
            private CoopRewardEntry() {
            }
        }

        @Entity
        public static class MainCoopData {
            @Getter @Setter private int id;
            @Getter @Setter private Map<Integer, Integer> normalVarMap;
            @Getter @Setter private List<Integer> savePointIdList;
            @Getter @Setter private Map<Integer, Integer> seenEndingMap;
            @Getter @Setter private int selfConfidence;
            @Getter @Setter private Status status;
            @Getter @Setter private Map<Integer, Integer> tempVarMap;

            private MainCoopData() {
                this.id = 0;
                this.normalVarMap = new HashMap<>();
                this.savePointIdList = new ArrayList<>();
                this.seenEndingMap = new HashMap<>();
                this.selfConfidence = 0;
                this.status = Status.UNRECOGNISED;
                this.tempVarMap = new HashMap<>();
            }

            public MainCoop toProto() {
                var mainCoop = new MainCoop();
                mainCoop.setId(this.id);
                mainCoop.setNormalVarMap(this.normalVarMap);
                mainCoop.setSavePointIdList(this.savePointIdList);
                mainCoop.setSeenEndingMap(this.seenEndingMap);
                mainCoop.setSelfConfidence(this.selfConfidence);
                mainCoop.setStatus(this.status);
                mainCoop.setTempVarMap(this.tempVarMap);
                return mainCoop;
            }

            public void fromProto(MainCoop mainCoop) {
                this.id = mainCoop.getId();
                this.normalVarMap = mainCoop.getNormalVarMap();
                this.savePointIdList = mainCoop.getSavePointIdList();
                this.seenEndingMap = mainCoop.getSeenEndingMap();
                this.selfConfidence = mainCoop.getSelfConfidence();
                this.status = mainCoop.getStatus();
                this.tempVarMap = mainCoop.getTempVarMap();
            }
        }
    }
}
