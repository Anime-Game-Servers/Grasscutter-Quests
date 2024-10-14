package emu.grasscutter.game.world;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.DataLoader;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.InvestigationMonsterData;
import emu.grasscutter.data.excels.RewardPreviewData;
import emu.grasscutter.data.excels.WorldLevelData;
import emu.grasscutter.game.entity.gadget.chest.BossChestInteractHandler;
import emu.grasscutter.game.entity.gadget.chest.ChestInteractHandler;
import emu.grasscutter.game.entity.gadget.chest.NormalChestInteractHandler;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.scripts.ScriptSystem;
import emu.grasscutter.server.game.BaseGameSystem;
import emu.grasscutter.server.game.GameServer;
import emu.grasscutter.utils.Position;
import lombok.val;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;
import org.anime_game_servers.gi_lua.models.scene.group.SceneMonster;
import org.anime_game_servers.multi_proto.gi.messages.world.investigation.InvestigationMonster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class WorldDataSystem extends BaseGameSystem {
    private final Map<String, ChestInteractHandler> chestInteractHandlerMap; // chestType-Handler
    private final Map<String, SceneGroup> sceneInvestigationGroupMap; // <sceneId_groupId, Group>

    public WorldDataSystem(GameServer server) {
        super(server);
        this.chestInteractHandlerMap = new HashMap<>();
        this.sceneInvestigationGroupMap = new ConcurrentHashMap<>();

        loadChestConfig();
    }

    public synchronized void loadChestConfig() {
        // set the special chest first
        chestInteractHandlerMap.put("SceneObj_Chest_Flora", new BossChestInteractHandler());

        try {
            DataLoader.loadList("ChestReward.json", ChestReward.class)
                .forEach(reward ->
                    reward.getObjNames().forEach(name ->
                        chestInteractHandlerMap.computeIfAbsent(name, x -> new NormalChestInteractHandler(reward))));
        } catch (Exception e) {
            Grasscutter.getLogger().error("Unable to load chest reward config.", e);
        }
    }

    public Map<String, ChestInteractHandler> getChestInteractHandlerMap() {
        return chestInteractHandlerMap;
    }

    public RewardPreviewData getRewardByBossId(int monsterId) {
        var investigationMonsterData = GameData.getInvestigationMonsterDataMap().values().parallelStream()
                .filter(imd -> imd.getMonsterIdList() != null && !imd.getMonsterIdList().isEmpty())
                .filter(imd -> imd.getMonsterIdList().get(0) == monsterId)
                .findFirst();

        if (investigationMonsterData.isEmpty()) {
            return null;
        }
        return GameData.getRewardPreviewDataMap().get(investigationMonsterData.get().getRewardPreviewId());
    }

    private SceneGroup getInvestigationGroup(int sceneId, int groupId) {
        val key = sceneId + "_" + groupId;
        val sceneMeta = getServer().getScriptSystem().getSceneMeta(sceneId);
        if (!sceneInvestigationGroupMap.containsKey(key)) {
            try{
                val group = sceneMeta.getGroup(groupId);
                if(group == null){
                    Grasscutter.getLogger().error("Null investigationGroup {} in scene{}:", groupId, sceneId);
                    return null;
                }
                group.load(ScriptSystem.getScriptLoader());
                sceneInvestigationGroupMap.putIfAbsent(key, group);
                return group;
            } catch (Exception luaError){
                Grasscutter.getLogger().error("failed to get investigationGroup {} in scene{}:", groupId, sceneId, luaError);
            }
        }
        return sceneInvestigationGroupMap.get(key);
    }

    public int getMonsterLevel(SceneMonster monster, World world) {
        // Calculate level
        int level = monster.getLevel();
        WorldLevelData worldLevelData = GameData.getWorldLevelDataMap().get(world.getWorldLevel());

        if (worldLevelData != null) {
            level = worldLevelData.getMonsterLevel();
        }
        return level;
    }

    private InvestigationMonster getInvestigationMonster(Player player, InvestigationMonsterData imd) {
        if (imd.getGroupIdList().isEmpty() || imd.getMonsterIdList().isEmpty()) {
            return null;
        }

        var groupId = imd.getGroupIdList().get(0);
        var monsterId = imd.getMonsterIdList().get(0);
        var sceneId = imd.getCityData().getSceneId();
        // scene id of the city doesn't match the scene id of the investigation group in some cases ( e.g. investigation data 37 has city 1 (scene 3) but group 155005095 is from scene 5)
        var group = getInvestigationGroup(sceneId, groupId);

        if (group == null || group.getMonsters() == null) {
            return null;
        }

        val monsterOpt = group.getMonsters().values().stream()
                .filter(x -> x.getMonsterId() == monsterId)
                .findFirst();
        if (monsterOpt.isEmpty()) {
            return null;
        }
        val monster = monsterOpt.get();
        val monsterPos = monster.getPos();
        if(monsterPos == null){
            Grasscutter.getLogger().error("null monster pos in investigationGroup {} in scene{}:", groupId, sceneId);
            return null;
        }

        var builder = new InvestigationMonster();

        builder.setId(imd.getId());
        builder.setCityId(imd.getCityId());
        builder.setSceneId(imd.getCityData().getSceneId());
        builder.setGroupId(groupId);
        builder.setMonsterId(monsterId);
        builder.setLevel(getMonsterLevel(monster, player.getWorld()));
        builder.setAlive(true);
        builder.setNextRefreshTime(Integer.MAX_VALUE);
        builder.setRefreshInterval(Integer.MAX_VALUE);
        builder.setPos(new Position(monsterPos).toProto());

        if ("Boss".equals(imd.getMonsterCategory())) {
            var bossChest = group.searchBossChestInGroup();
            if (bossChest.isPresent()) {
                builder.setResin(bossChest.get().getResin());
                builder.setBossChestNum(bossChest.get().getTakeNum());
            }
        }
        return builder;
    }

    public List<InvestigationMonster> getInvestigationMonstersByCityId(Player player, int cityId) {
        var cityData = GameData.getCityDataMap().get(cityId);
        if (cityData == null) {
            Grasscutter.getLogger().warn("City not exist {}", cityId);
            return List.of();
        }

        return GameData.getInvestigationMonsterDataMap().values()
                .parallelStream()
                .filter(imd -> imd.getCityId() == cityId)
                .map(imd -> this.getInvestigationMonster(player, imd))
                .filter(Objects::nonNull)
                .toList();
    }

}
