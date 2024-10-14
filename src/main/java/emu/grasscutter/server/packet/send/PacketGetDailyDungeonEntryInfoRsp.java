package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.DungeonEntryData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.enums.LogicType;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.entry.DailyDungeonEntryInfo;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.entry.GetDailyDungeonEntryInfoRsp;
import org.anime_game_servers.core.gi.enums.QuestState;

import java.util.*;

public class PacketGetDailyDungeonEntryInfoRsp extends BaseTypedPacket<GetDailyDungeonEntryInfoRsp> {

    public PacketGetDailyDungeonEntryInfoRsp(Player player, int sceneId) {
        super(new GetDailyDungeonEntryInfoRsp());

        proto.setDailyDungeonInfoList(GameData.getDungeonEntryDataMap().values().stream()
                .filter(data -> data.getSceneId() == sceneId)
                .filter(DungeonEntryData::isShowInAdvHandbook)
                .filter(data -> isSatisfied(player, data.getCondComb(), data.getSatisfiedCond()))
                .map(data -> getDungeonEntryInfo(player, data))
                .filter(e -> e.getRecommendDungeonEntryInfo().getDungeonId() != 0)
                .sorted(Comparator.comparing(DailyDungeonEntryInfo::getDungeonEntryConfigId))
                .toList());
    }

    private DailyDungeonEntryInfo getDungeonEntryInfo(Player player, DungeonEntryData entryData) {
        val dailyEntryInfo = new DailyDungeonEntryInfo(entryData.getDungeonEntryId(), entryData.getId());
        player.getDungeonEntryManager().getDungeonEntries(entryData.getSceneId(), entryData.getDungeonEntryId()).stream()
            .min(Comparator.comparingInt(data -> Math.abs(data.getLimitLevel() - player.getLevel())))
            .ifPresent(dungeonData -> {
                dailyEntryInfo.setRecommendDungeonId(dungeonData.getId());
                dailyEntryInfo.setRecommendDungeonEntryInfo(player.getDungeonEntryManager().toProto(dungeonData));
            });

        return dailyEntryInfo;
    }

    private boolean isSatisfied(Player player, LogicType condComb, List<DungeonEntryData.SatisfiedCond> conds) {
        return LogicType.calculate(condComb, conds.stream()
            .filter(cond -> Optional.ofNullable(cond).map(DungeonEntryData.SatisfiedCond::getType).isPresent())
            .map(cond -> switch (cond.getType()) {
                case DUNGEON_ENTRY_CONDITION_LEVEL -> player.getLevel() >= cond.getParam1();
                case DUNGEON_ENTRY_CONDITION_QUEST -> Optional.ofNullable(player.getQuestManager().getQuestById(cond.getParam1()))
                    .map(GameQuest::getState).filter(state -> state == QuestState.QUEST_STATE_FINISHED).isPresent();
                case DUNGEON_ENTRY_CONDITION_NONE -> true;})
            .mapToInt(result -> result ? 1 : 0)
            .toArray());
    }
}
