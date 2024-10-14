package emu.grasscutter.game.dungeons.dungeon_results;

import emu.grasscutter.data.excels.DungeonData;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.dungeons.DungeonEndStats;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.inventory.ItemType;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.utils.Utils;
import lombok.Builder;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.StrengthenPointData;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.progression.DungeonSettleNotify;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.progression.ParamList;

import java.util.*;
import java.util.stream.Collectors;

import static emu.grasscutter.game.dungeons.enums.DungeonInvolveType.INVOLVE_SINGLE_MULTIPLE;

/**
 * Shows dungeon results
 * */
public class BaseDungeonResult {
    private final DungeonData dungeonData;
    @Getter private final DungeonEndStats dungeonStats;
    private final Player player;

    @Builder(builderMethodName = "BaseBuilder", setterPrefix = "set")
    protected BaseDungeonResult(DungeonData dungeonData, DungeonEndStats dungeonStats, Player player){
        this.dungeonData = dungeonData;
        this.dungeonStats = dungeonStats;
        this.player = player;
    }

    /**
     * Could be different depending on dungeon types
     * */
    protected void onProto(DungeonSettleNotify builder) {
    }

    /**
     * Show player's area to improve, not completed
     * */
    private void getStrengthenPointData(DungeonSettleNotify builder) {
        if (this.dungeonStats.dungeonResult().isSuccess() ||
            this.dungeonData.getInvolveType() != INVOLVE_SINGLE_MULTIPLE) return;

        val playerActiveTeam = this.player.getTeamManager().getActiveTeam();
        builder.setStrengthenPointDataMap(Arrays.stream(StrengthenPointType.values()).collect(
            Collectors.toMap(StrengthenPointType::getValue, type -> switch (type) {
                case LEVEL -> {
                    val strengthenPointData = new StrengthenPointData();
                    strengthenPointData.setBasePoint(playerActiveTeam.size() * 90);
                    strengthenPointData.setCurPoint(playerActiveTeam.stream()
                        .map(EntityAvatar::getAvatar)
                        .map(Avatar::getLevel)
                        .reduce(0, Integer::sum));
                    yield strengthenPointData;
                }
                case WEAPON -> {
                    val strengthenPointData = new StrengthenPointData();
                    strengthenPointData.setBasePoint(playerActiveTeam.size() * 90);
                    strengthenPointData.setCurPoint(playerActiveTeam.stream()
                        .map(EntityAvatar::getAvatar)
                        .map(Avatar::getEquips)
                        .map(Map::values)
                        .flatMap(Collection::stream)
                        .filter(item -> item.getItemType() == ItemType.ITEM_WEAPON)
                        .map(GameItem::getLevel)
                        .reduce(0, Integer::sum));
                    yield strengthenPointData;
                }
                case TALENT -> {
                    val strengthenPointData = new StrengthenPointData();
                    strengthenPointData.setBasePoint(100000);
                    strengthenPointData.setCurPoint(50000);
                    yield strengthenPointData;
                }
                case ARTIFACT -> {
                    val strengthenPointData = new StrengthenPointData();
                    strengthenPointData.setBasePoint(playerActiveTeam.size() * 20);
                    strengthenPointData.setCurPoint(playerActiveTeam.stream()
                        .map(EntityAvatar::getAvatar)
                        .map(Avatar::getEquips)
                        .map(Map::values)
                        .flatMap(Collection::stream)
                        .filter(item -> item.getItemType() == ItemType.ITEM_RELIQUARY)
                        .map(GameItem::getLevel)
                        .reduce(0, Integer::sum));
                    yield strengthenPointData;
                }
            })));
    }

    public final DungeonSettleNotify getProto() {
        val success = this.dungeonStats.getDungeonResult().isSuccess();
        val builder = new DungeonSettleNotify();
        builder.setUseTime(this.dungeonStats.getTimeTaken());
        builder.setDungeonId(this.dungeonData.getId());
        builder.setSuccess(success);
        builder.setCloseTime(getCloseTime());
        builder.setResult(success ? 1 : 3);
        builder.setCreatePlayerUid(this.player.getUid());

        // TODO check
        val tempSettleMap = new HashMap<Integer, ParamList>();
        Optional.ofNullable(this.dungeonData.getSettleShows()).stream()
            .flatMap(List::stream)
            .forEach(showType -> {
                val paramList = tempSettleMap.computeIfAbsent(showType.getId(), f -> new ParamList());
                val paramListList = new ArrayList<>(paramList.getParamList());
                paramListList.add(switch (showType) {
                    case SETTLE_SHOW_TIME_COST -> this.dungeonStats.getTimeTaken();
                    case SETTLE_SHOW_KILL_MONSTER_COUNT -> this.dungeonStats.getKilledMonsters();
                    case SETTLE_SHOW_OPEN_CHEST_COUNT -> this.dungeonStats.getOpenChestCount();
                    default -> 0;
                });
                paramList.setParamList(paramListList);
                tempSettleMap.put(showType.getId(), paramList);
            });
        builder.setSettleShow(tempSettleMap);

        getStrengthenPointData(builder);
        onProto(builder);
        return builder;
    }

    private int getCloseTime(){
        return Utils.getCurrentSeconds() + switch (this.dungeonStats.getDungeonResult()){
            case COMPLETED -> this.dungeonData.getSettleCountdownTime();
            case FAILED -> this.dungeonData.getFailSettleCountdownTime();
            case QUIT -> this.dungeonData.getQuitSettleCountdownTime();
        };
    }

    public enum DungeonEndReason{
        COMPLETED,
        FAILED,
        QUIT;

        public boolean isSuccess(){
            return this == COMPLETED;
        }
    }

    @Getter
    private enum StrengthenPointType {
        LEVEL(1),
        WEAPON(2),
        ARTIFACT(3),
        TALENT(4);

        private final int value;
        StrengthenPointType (int value){
            this.value = value;
        }
    }
}
