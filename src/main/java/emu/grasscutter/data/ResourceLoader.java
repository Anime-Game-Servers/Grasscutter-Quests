package emu.grasscutter.data;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import emu.grasscutter.Loggers;
import emu.grasscutter.data.binout.*;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.data.binout.config.*;
import emu.grasscutter.data.binout.config.fields.ConfigAbilityData;
import emu.grasscutter.data.binout.routes.SceneRoutes;
import emu.grasscutter.data.common.PointData;
import emu.grasscutter.data.common.ScenePointArrayData;
import emu.grasscutter.data.common.WeatherAreaPointData;
import emu.grasscutter.data.common.quest.MainQuestData;
import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.custom.TrialAvatarActivityCustomData;
import emu.grasscutter.data.custom.TrialAvatarCustomData;
import emu.grasscutter.data.excels.TrialAvatarActivityDataData;
import emu.grasscutter.data.server.*;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.dungeons.DungeonDrop;
import emu.grasscutter.game.dungeons.dungeon_entry.DungeonEntries;
import emu.grasscutter.game.dungeons.enums.DungeonType;
import emu.grasscutter.game.managers.blossom.BlossomConfig;
import emu.grasscutter.game.quest.QuestEncryptionKey;
import emu.grasscutter.game.quest.enums.QuestCond;
import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.game.world.SpawnDataEntry;
import emu.grasscutter.game.world.SpawnDataEntry.GridBlockId;
import emu.grasscutter.game.world.SpawnDataEntry.SpawnGroupEntry;
import emu.grasscutter.scripts.EntityControllerScriptManager;
import emu.grasscutter.scripts.SceneIndexManager;
import emu.grasscutter.scripts.ScriptSystem;
import emu.grasscutter.utils.FileUtils;
import emu.grasscutter.utils.JsonUtils;
import emu.grasscutter.utils.TsvUtils;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import lombok.val;
import org.anime_game_servers.gi_lua.models.loader.SceneReplacementScriptLoadParams;
import org.anime_game_servers.gi_lua.models.loader.ShardQuestScriptLoadParams;
import org.anime_game_servers.gi_lua.models.quest.QuestData;
import org.anime_game_servers.gi_lua.models.quest.RewindData;
import org.anime_game_servers.gi_lua.models.scene.SceneGroupReplacement;
import org.reflections.Reflections;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static emu.grasscutter.utils.FileUtils.getDataPath;
import static emu.grasscutter.utils.FileUtils.getResourcePath;
import static emu.grasscutter.utils.Language.translate;

public class ResourceLoader {
    private static final Logger logger = Loggers.getResourceSystem();

    private static final Set<String> loadedResources = new CopyOnWriteArraySet<>();

    // Get a list of all resource classes, sorted by loadPriority
    public static List<Class<?>> getResourceDefClasses() {
        Reflections reflections = new Reflections(ResourceLoader.class.getPackage().getName());
        Set<?> classes = reflections.getSubTypesOf(GameResource.class);

        List<Class<?>> classList = new ArrayList<>(classes.size());
        classes.forEach(o -> {
            Class<?> c = (Class<?>) o;
            if (c.getAnnotation(ResourceType.class) != null) {
                classList.add(c);
            }
        });

        classList.sort((a, b) -> b.getAnnotation(ResourceType.class).loadPriority().value() - a.getAnnotation(ResourceType.class).loadPriority().value());

        return classList;
    }

    // Get a list containing sets of all resource classes, sorted by loadPriority
    protected static List<Set<Class<?>>> getResourceDefClassesPrioritySets() {
        val reflections = new Reflections(ResourceLoader.class.getPackage().getName());
        val classes = reflections.getSubTypesOf(GameResource.class);
        val priorities = ResourceType.LoadPriority.getInOrder();
        logger.debug("Priorities are {}", priorities);
        val map = new LinkedHashMap<ResourceType.LoadPriority, Set<Class<?>>>(priorities.size());
        priorities.forEach(p -> map.put(p, new HashSet<>()));

        classes.forEach(c -> {
            // val c = (Class<?>) o;
            val annotation = c.getAnnotation(ResourceType.class);
            if (annotation != null) {
                map.get(annotation.loadPriority()).add(c);
            }
        });
        return List.copyOf(map.values());
    }

    private static boolean loadedAll = false;
    public static void loadAll() {
        if (loadedAll) return;
        logger.info(translate("messages.status.resources.loading"));

        loadConfigData();
        // Load ability lists
        loadAbilityEmbryos();
        loadTalents();
        loadOpenConfig();
        loadAbilityModifiers();
        // Load resources
        loadResources(true);
        // Process into depots
        GameDepot.load();
        // Load spawn data and quests
        loadSceneRoutes();
        loadScenePointArrays();
        loadSpawnData();
        loadQuests();
        loadScriptSceneData();
        loadDungeonDrops();
        // Load scene points - must be done AFTER resources are loaded
        loadScenePoints();
        loadSceneWeatherAreas();
        loadDungeonEntryAndExitPoints();
        // Load default home layout
        loadHomeworldDefaultSaveData();
        loadNpcBornData();
        loadBlossomResources();
        cacheTalentLevelSets();
        // Load special ability in certain scene/dungeon
        loadConfigLevelEntityData();
        loadScriptData();
        loadGadgetMappings();
        loadSubfieldMappings();
        loadWeatherMappings();
        loadMonsterMappings();
        loadActivityCondGroups();
        loadTrialAvatarCustomData();
        loadGlobalCombatConfig();
        EntityControllerScriptManager.load();
        logger.info(translate("messages.status.resources.finish"));
        loadedAll = true;
    }

    public static void loadResources() {
        loadResources(false);
    }

    public static void loadResources(boolean doReload) {
        long startTime = System.nanoTime();
        val errors = new ConcurrentLinkedQueue<Pair<String, Exception>>();  // Logger in a parallel stream will deadlock

        getResourceDefClassesPrioritySets().forEach(classes -> {
            classes.stream()
                .parallel().unordered()
                .forEach(c -> {
                    val type = c.getAnnotation(ResourceType.class);
                    if (type == null) return;

                    val map = GameData.getMapByResourceDef(c);
                    if (map == null) return;

                    try {
                        loadFromResource(c, type, map, doReload);
                    } catch (Exception e) {
                        errors.add(Pair.of(Arrays.toString(type.name()), e));
                    }
                });
        });
        errors.forEach(pair -> logger.error("Error loading resource file: " + pair.left(), pair.right()));
        long endTime = System.nanoTime();
        long ns = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        logger.debug("Loading resources took {}ns == {}ms", ns, ns/1000000);
    }

    @SuppressWarnings("rawtypes")
    protected static void loadFromResource(Class<?> c, ResourceType type, Int2ObjectMap map, boolean doReload) throws Exception {
        val simpleName = c.getSimpleName();
        if (doReload || !loadedResources.contains(simpleName)) {
            for (String name : type.name()) {
                loadFromResource(c, FileUtils.getExcelPath(name), map);
            }
            loadedResources.add(simpleName);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected static <T> void loadFromResource(Class<T> c, Path filename, Int2ObjectMap map) throws Exception {
        val results = switch (FileUtils.getFileExtension(filename)) {
            case "json" -> JsonUtils.loadToList(filename, c);
            case "tsj" -> TsvUtils.loadTsjToListSetField(filename, c);
            case "tsv" -> TsvUtils.loadTsvToListSetField(filename, c);
            default -> null;
        };
        if (results == null) return;
        results.forEach(o -> {
            GameResource res = (GameResource) o;
            res.onLoad();
            map.put(res.getId(), res);
        });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected static <T> void loadFromResource(Class<T> c, String fileName, Int2ObjectMap map) throws Exception {
        JsonUtils.loadToList(getResourcePath("ExcelBinOutput/" + fileName), c).forEach(o -> {
            GameResource res = (GameResource) o;
            res.onLoad();
            map.put(res.getId(), res);
        });
    }

    private static void loadGlobalCombatConfig(){
        try {
            GameData.setConfigGlobalCombat(JsonUtils.loadToClass(getResourcePath("BinOutput/Common/ConfigGlobalCombat.json"), ConfigGlobalCombat.class));
        } catch (IOException e) {
            logger.error("Cannot load ConfigGlobalCombat.json, this error is important, fix it!");
        }
    }

    public static class ScenePointConfig {  // Sadly this doesn't work as a local class in loadScenePoints()
        public Map<Integer, PointData> points;
    }
    private static void loadScenePoints() {
        val pattern = Pattern.compile("scene([0-9]+)_point\\.json");
        try (val stream = Files.newDirectoryStream(getResourcePath("BinOutput/Scene/Point"), "scene*_point.json")){
            stream.forEach(path -> {
                val matcher = pattern.matcher(path.getFileName().toString());
                if (!matcher.find()) return;
                int sceneId = Integer.parseInt(matcher.group(1));

                ScenePointConfig config;
                try {
                    config = JsonUtils.loadToClass(path, ScenePointConfig.class);
                } catch (Exception e) {
                    logger.error("Error loading scene point file: {}", path, e);
                    return;
                }

                if (config.points == null) return;

                val scenePoints = new IntArrayList();
                config.points.forEach((pointId, pointData) -> {
                    val scenePoint = new ScenePointEntry(sceneId, pointData);
                    scenePoints.add(pointId.intValue());
                    pointData.setId(pointId);
                    pointData.setSceneId(sceneId);

                    GameData.getScenePointIdList().add(pointId.intValue());
                    GameData.getScenePointEntries().put(scenePoint.getName(), scenePoint);
                    GameData.scenePointEntryMap.put((sceneId << 16) + pointId, scenePoint);

                    pointData.onLoad();
                    pointData.updateDailyDungeon();
                });
                GameData.getScenePointsPerScene().put(sceneId, scenePoints);
            });
        } catch (IOException e) {
            logger.error("Scene point files cannot be found, you cannot use teleport waypoints!");
        }
    }

    private static void loadSceneWeatherAreas() {
        val pattern = Pattern.compile("scene([0-9]+)_weather_areas\\.json");
        try (val dirStream = Files.newDirectoryStream(getResourcePath("Scripts/Scene"))) {
            dirStream.forEach(dirPath -> {
                try (val stream = Files.newDirectoryStream(dirPath, "scene*_weather_areas.json")){
                    stream.forEach(path -> {
                        val matcher = pattern.matcher(path.getFileName().toString());
                        if (!matcher.find()) return;
                        int sceneId = Integer.parseInt(matcher.group(1));

                        List<WeatherAreaPointData> config;
                        try {
                            config = JsonUtils.loadToList(path, WeatherAreaPointData.class);
                        } catch (Exception e) {
                            logger.error("Error loading weather area file: {}", path, e);
                            return;
                        }

                        if (config == null) return;

                        config.forEach((area) -> {
                            GameData.getWeatherAreaPointData().put(sceneId, config);
                        });
                    });
                } catch (IOException e) {
                    logger.error("Weather area files cannot be found in {}", dirPath);
                }
            });
        } catch (IOException e) {
            logger.error("Weather area files cannot be found");
        }
    }

    /**
     * Pre-make dungeon entries and exits using existing map for easier information retrieve
     * Some dungeon exits not included like trial avatar activity and mist trial activity, player
     * should transfer back to where they were
     * TODO there are 2 special dungeon entries not included so far, which is amber dungeon(2002) and one of summerV2 event dungeon(4038)
     * */
    private static void loadDungeonEntryAndExitPoints() {
        val tempEntriesMap = GameData.getDungeonEntriesMap();

        val tempExitHolderMap = GameData.scenePointEntryMap.values().stream().parallel().map(ScenePointEntry::getPointData)
            .filter(pointData -> pointData.getType().equals("DungeonExit")).filter(pointData -> pointData.getEntryPointId() > 0)
            .collect(Collectors.toMap(pointData -> (pointData.getSceneId() << 16) + pointData.getEntryPointId(), pointData -> pointData));

        GameData.scenePointEntryMap.values().stream().map(ScenePointEntry::getPointData)
            .filter(pointData -> pointData.getType().equals("DungeonEntry"))
            .forEach(pointData -> {
                val loadedDungeonIds = new AtomicBoolean(false);
                Optional.ofNullable(pointData.getAllDungeonIds()).stream().flatMap(List::stream).forEach(dungeonId -> {
                    loadedDungeonIds.set(true);
                    tempEntriesMap.putIfAbsent(dungeonId, DungeonEntries.create(dungeonId, pointData, tempExitHolderMap));
                });
                if (loadedDungeonIds.get()) return;

                val dungeonType = switch (pointData.getDungeonEntryType()) {
                    case "Tower" -> DungeonType.DUNGEON_TOWER;
                    case "RogueDiary" -> DungeonType.DUNGEON_ROGUELIKE;
                    case "Effigy" -> DungeonType.DUNGEON_EFFIGY;
                    default -> DungeonType.DUNGEON_NONE;
                };
                GameData.getDungeonDataMap().values().stream().filter(dungeonData -> dungeonData.getType() == dungeonType)
                    .forEach(dungeonData -> tempEntriesMap.putIfAbsent(
                        dungeonData.getId(), DungeonEntries.create(dungeonData.getId(), pointData, tempExitHolderMap)));
            });

        GameData.getQuestDataMap().values().forEach(data -> Optional.ofNullable(data.getGuide())
            .map(SubQuestData.Guide::getGuideScene).ifPresent(sceneId -> data.getFinishCond().stream()
                .filter(c -> c.getType() == QuestContent.QUEST_CONTENT_ENTER_DUNGEON)
                .filter(c -> !tempEntriesMap.containsKey(c.getParam()[0])).forEach(cond -> {
                    val scenePointEntry = GameData.getScenePointEntryById(sceneId == 0 ? 3 : sceneId, cond.getParam()[1]);
                    if (scenePointEntry == null) {
//                        Grasscutter.getLogger().info("MainQuest: {}, SubQuest: {}", data.getMainId(), data.getSubId());
//                        Grasscutter.getLogger().info("SceneId: {}, PointId: {}, dungeonId: {}", sceneId, cond.getParam()[1], cond.getParam()[0]);
                        return;
                    }

                    tempEntriesMap.putIfAbsent(cond.getParam()[0],
                        DungeonEntries.create(cond.getParam()[0], scenePointEntry.getPointData(), tempExitHolderMap));
                })));
    }

    private static void cacheTalentLevelSets() {
        // All known levels, keyed by proudSkillGroupId
        GameData.getProudSkillDataMap().forEach((id, data) ->
            GameData.proudSkillGroupLevels
                .computeIfAbsent(data.getProudSkillGroupId(), i -> new IntArraySet())
                .add(data.getLevel()));
        // All known levels, keyed by avatarSkillId
        GameData.getAvatarSkillDataMap().forEach((id, data) ->
            GameData.avatarSkillLevels.put((int) id, GameData.proudSkillGroupLevels.get(data.getProudSkillGroupId())));
        // Maximum known levels, keyed by proudSkillGroupId
        GameData.proudSkillGroupLevels.forEach((id, set) ->
            GameData.proudSkillGroupMaxLevels.put((int) id, set.intStream().max().getAsInt()));
    }


    private static void loadAbilityEmbryos() {
        List<AbilityEmbryoEntry> embryoList = null;

        // Read from cached file if exists
        try {
            embryoList = JsonUtils.loadToList(getDataPath("AbilityEmbryos.json"), AbilityEmbryoEntry.class);
        } catch (Exception ignored) {}

        if (embryoList == null) {
            val pattern = Pattern.compile("ConfigAvatar_(.+)");
            val l = new ArrayList<AbilityEmbryoEntry>();
            // Load from BinOutput
            GameData.getAvatarConfigData().forEach((key, config) -> {
                val abilities = config.getAbilities();
                val matcher = pattern.matcher(key);
                if (abilities == null || !matcher.find()) {
                    return;
                }
                String avatarName = matcher.group(1);
                int s = abilities.size();
                AbilityEmbryoEntry al = new AbilityEmbryoEntry(avatarName, abilities.stream().map(ConfigAbilityData::getAbilityName).toArray(size -> new String[s]));
                l.add(al);
            });

            embryoList = l;

            try {
                GameDepot.setPlayerAbilities(JsonUtils.loadToMap(getResourcePath("BinOutput/AbilityGroup/AbilityGroup_Other_PlayerElementAbility.json"), String.class, AbilityGroup.class));
            } catch (IOException e) {
                logger.error("Error loading player abilities:", e);
            }
        }

        if (embryoList.isEmpty()) {
            logger.error("No embryos loaded!");
            return;
        }

        for (AbilityEmbryoEntry entry : embryoList) {
            GameData.getAbilityEmbryoInfo().put(entry.getName(), entry);
        }
    }

    // private static HashSet<String> modifierActionTypes = new HashSet<>();
    public static class AbilityConfigData {
        public AbilityData Default;
    }
    private static void loadAbilityModifiers() {
        // Load from BinOutput
        try (Stream<Path> paths = Files.walk(getResourcePath("BinOutput/Ability/Temp/"))) {
            paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".json")).forEach(ResourceLoader::loadAbilityModifiers);
        } catch (IOException e) {
            logger.error("Error loading ability modifiers: ", e);
        }
        // System.out.println("Loaded modifiers, found types:");
        // modifierActionTypes.stream().sorted().forEach(s -> System.out.printf("%s, ", s));
        // System.out.println("[End]");
    }
    private static void loadAbilityModifiers(Path path) {
        try {
            JsonUtils.loadToList(path, AbilityConfigData.class).forEach(data -> loadAbilityData(data.Default));
        } catch (IOException e) {
            logger.error("Error loading ability modifiers from path " + path.toString() + ": ", e);
        }
    }
    private static void loadAbilityData(AbilityData data) {
        GameData.abilityDataMap.put(data.abilityName, data);
        GameData.getAbilityHashes().put(Ability.AbilityHash(data.abilityName), data.abilityName);

        val modifiers = data.modifiers;
        if (modifiers == null || modifiers.size() == 0) return;

        String name = data.abilityName;
        AbilityModifierEntry modifierEntry = new AbilityModifierEntry(name);
        modifiers.forEach((key, modifier) -> {
            Stream.ofNullable(modifier.onAdded).flatMap(Stream::of)
                // .map(action -> {modifierActionTypes.add(action.$type); return action;})
                .filter(action -> action.type == AbilityModifierAction.Type.HealHP)
                .forEach(action -> modifierEntry.getOnAdded().add(action));
            Stream.ofNullable(modifier.onThinkInterval).flatMap(Stream::of)
                // .map(action -> {modifierActionTypes.add(action.$type); return action;})
                .filter(action -> action.type == AbilityModifierAction.Type.HealHP)
                .forEach(action -> modifierEntry.getOnThinkInterval().add(action));
            Stream.ofNullable(modifier.onRemoved).flatMap(Stream::of)
                // .map(action -> {modifierActionTypes.add(action.$type); return action;})
                .filter(action -> action.type == AbilityModifierAction.Type.HealHP)
                .forEach(action -> modifierEntry.getOnRemoved().add(action));
        });

        GameData.getAbilityModifiers().put(name, modifierEntry);
    }

    private static void loadTalents() {
        // Load from BinOutput
        try (Stream<Path> paths = Files.walk(getResourcePath("BinOutput/Talent/AvatarTalents/"))) {
            paths.filter(Files::isDirectory).forEach((folderPath) -> {
                try (Stream<Path> paths2 = Files.walk(folderPath)) {
                    paths2.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".json")).forEach(ResourceLoader::loadTalent);
                } catch (IOException e) {
                    logger.error("Error loading talents: ", e);
                }
            });
        } catch (IOException e) {
            logger.error("Error loading talents: ", e);
        }
    }

    private static void loadTalent(Path path) {
        try {
            GameData.getTalents().putAll(JsonUtils.loadToMap(path, String.class, new TypeToken<List<TalentData>>() {}.getType()));
        } catch (IOException e) {
            logger.error("Error loading ability modifiers from path " + path.toString() + ": ", e);
        }
    }

    private static void loadSpawnData() {
        String[] spawnDataNames = {"Spawns.json", "GadgetSpawns.json"};
        ArrayList<SpawnGroupEntry> spawnEntryMap = new ArrayList<>();

        for (String name : spawnDataNames) {
            // Load spawn entries from file
            try (InputStreamReader reader = DataLoader.loadReader(name)) {
                // Add spawns to group if it already exists in our spawn group map
                spawnEntryMap.addAll(JsonUtils.loadToList(reader, SpawnGroupEntry.class));
            } catch (Exception ignored) {}
        }

        if (spawnEntryMap.isEmpty()) {
            logger.error("No spawn data loaded!");
            return;
        }

        HashMap<GridBlockId, ArrayList<SpawnDataEntry>> areaSort = new HashMap<>();
        //key = sceneId,x,z , value = ArrayList<SpawnDataEntry>
        for (SpawnGroupEntry entry : spawnEntryMap) {
            entry.getSpawns().forEach(
                s -> {
                    s.setGroup(entry);
                    GridBlockId point = s.getBlockId();
                    if (!areaSort.containsKey(point)) {
                        areaSort.put(point, new ArrayList<>());
                    }
                    areaSort.get(point).add(s);
                }
            );
        }
        GameDepot.addSpawnListById(areaSort);
    }

    private static void loadDungeonDrops(){
        try {
            DataLoader.loadList("DungeonDrop.json", DungeonDrop.class).forEach(entry -> {
                GameData.getDungeonDropDataMap().put(entry.getDungeonId(), entry.getDrops());
            });
            logger.debug("Loaded {} dungeon drop data entries.", GameData.getDungeonDropDataMap().size());
        }
        catch (Exception ex) {
            logger.error("Unable to load dungeon drop data.", ex);
        }
    }

    private static void loadOpenConfig() {
        // Read from cached file if exists
        List<OpenConfigEntry> list = null;

        try {
            list = JsonUtils.loadToList(getDataPath("OpenConfig.json"), OpenConfigEntry.class);
        } catch (Exception ignored) {}

        if (list == null) {
            Map<String, OpenConfigEntry> map = new TreeMap<>();
            String[] folderNames = {"BinOutput/Talent/EquipTalents/", "BinOutput/Talent/AvatarTalents/"};

            for (String folderName : folderNames) {
                try (val stream = Files.newDirectoryStream(getResourcePath(folderName), "*.json")){
                    stream.forEach(path -> {
                        try {
                            JsonUtils.loadToMap(path, String.class, OpenConfigData[].class)
                                .forEach((name, data) -> map.put(name, new OpenConfigEntry(name, data)));
                        } catch (Exception e) {
                            logger.warn("Error loading open config from {}", path, e);
                            return;
                        }
                    });
                } catch (IOException e) {
                    logger.error("Error loading open config: no files found in " + folderName);
                    return;
                }
            }

            list = new ArrayList<>(map.values());
        }

        if (list.isEmpty()) {
            logger.error("No openconfig entries loaded!");
            return;
        }

        for (OpenConfigEntry entry : list) {
            GameData.getOpenConfigEntries().put(entry.getName(), entry);
        }
    }

    private static void loadQuests() {
        try (val stream = Files.list(getResourcePath("Generated/Quest/"))) {
            stream.forEach(path -> {
                try {
                    val mainQuest = JsonUtils.loadToClass(path, MainQuestData.class);
                    GameData.getMainQuestDataMap().put(mainQuest.getId(), mainQuest);
                    if(mainQuest.getTalks() != null) {
                        mainQuest.getTalks().forEach(talkData -> GameData.getQuestTalkMap().put(talkData.getId(), mainQuest.getId()));
                    }
                    for(SubQuestData quest : mainQuest.getSubQuests()){
                        addToCache(quest);
                    }
                } catch (IOException e) {

                }
            });
        } catch (IOException e) {
            logger.error("Quest data missing");
            return;
        }

        try {
            val questEncryptionMap = GameData.getMainQuestEncryptionMap();
            String path = "QuestEncryptionKeys.json";
            try {
                JsonUtils.loadToList(getResourcePath(path), QuestEncryptionKey.class).forEach(key -> questEncryptionMap.put(key.getMainQuestId(), key));
            } catch (IOException | NullPointerException ignored) {}
            try {
                DataLoader.loadList(path, QuestEncryptionKey.class).forEach(key -> questEncryptionMap.put(key.getMainQuestId(), key));
            } catch (IOException | NullPointerException ignored) {}
            logger.debug("Loaded {} quest keys.", questEncryptionMap.size());
        } catch (Exception e) {
            logger.error("Unable to load quest keys.", e);
        }

        logger.debug("Loaded " + GameData.getMainQuestDataMap().size() + " MainQuestDatas.");
    }

    private static void addToCache(SubQuestData questData) {
        GameData.getQuestDataMap().put(questData.getSubId(), questData);
        if (questData.getAcceptCond() == null) {
            logger.warn("missing AcceptConditions for quest {}", questData.getSubId());
            return;
        }
        val cacheMap = GameData.getBeginCondQuestMap();
        if (questData.getAcceptCond().isEmpty()) {
            val list = cacheMap.computeIfAbsent(SubQuestData.questConditionKey(QuestCond.QUEST_COND_NONE, 0, null), e -> new ArrayList<>());
            list.add(questData);
        } else {
            questData.getAcceptCond().forEach(questCondition -> {
                if (questCondition.getType() == null) {
                    logger.warn("null accept type for quest {}", questData.getSubId());
                    return;
                }
                val key = questCondition.asKey();
                val list = cacheMap.computeIfAbsent(key, e -> new ArrayList<>());
                list.add(questData);
            });
        }
    }

    public static void loadScriptSceneData() {
        try (val stream = Files.list(getResourcePath("ScriptSceneData/"))) {
            stream.forEach(path -> {
                try {
                    GameData.getScriptSceneDataMap().put(path.getFileName().toString(), JsonUtils.loadToClass(path, ScriptSceneData.class));
                } catch (IOException e) {
                    logger.warn("Error loading ScriptSceneData from {}", path, e);
                    return;
                }
            });
            logger.debug("Loaded {} ScriptSceneDatas.", GameData.getScriptSceneDataMap().size());
        } catch (IOException e) {
            logger.debug("ScriptSceneData folder missing or empty.");
            return;
        }
    }

    private static void loadHomeworldDefaultSaveData() {
        val pattern = Pattern.compile("scene([0-9]+)_home_config\\.json");
        try( val stream =  Files.newDirectoryStream(getResourcePath("BinOutput/HomeworldDefaultSave"), "scene*_home_config.json")) {
           stream.forEach(path -> {
                val matcher = pattern.matcher(path.getFileName().toString());
                if (!matcher.find()) return;

                try {
                    val sceneId = Integer.parseInt(matcher.group(1));
                    val data = JsonUtils.loadToClass(path, HomeworldDefaultSaveData.class);
                    GameData.getHomeworldDefaultSaveData().put(sceneId, data);
                } catch (Exception ignored) {}
            });
            logger.debug("Loaded {} HomeworldDefaultSaveDatas.", GameData.getHomeworldDefaultSaveData().size());
        } catch (IOException e) {
            logger.error("Failed to load HomeworldDefaultSave folder.");
        }
    }

    private static void loadNpcBornData() {
        try (val stream = Files.newDirectoryStream(getResourcePath("BinOutput/Scene/SceneNpcBorn/"), "*.json")){
            stream.forEach(path -> {
                try {
                    val data = JsonUtils.loadToClass(path, SceneNpcBornData.class);
                    if (data.getBornPosList() == null || data.getBornPosList().size() == 0) {
                        return;
                    }

                    data.setIndex(SceneIndexManager.buildIndex(3, data.getBornPosList(), item -> item.getPos().toPoint()));
                    GameData.getSceneNpcBornData().put(data.getSceneId(), data);
                } catch (IOException ignored) {}
            });
            logger.debug("Loaded {} SceneNpcBornDatas.", GameData.getSceneNpcBornData().size());
        } catch (IOException e) {
            logger.error("Failed to load SceneNpcBorn folder.");
        }
    }
    private static void loadConfigData(){
        loadConfigData(GameData.getAvatarConfigData(), "BinOutput/Avatar/", ConfigEntityAvatar.class);
        loadConfigData(GameData.getMonsterConfigData(), "BinOutput/Monster/", ConfigEntityMonster.class);
        loadConfigDataMap(GameData.getGadgetConfigData(), "BinOutput/Gadget/", ConfigEntityGadget.class);
    }

    private static <T extends ConfigEntityBase> void loadConfigDataMap(Map<String,T> targetMap, String folderPath, Class<T> configClass) {
        val className = configClass.getName();
        try(val stream = Files.newDirectoryStream(getResourcePath(folderPath), "*.json")) {
            stream.forEach(path -> {
                try {
                    targetMap.putAll(JsonUtils.loadToMap(path, String.class, configClass));
                } catch (Exception e) {
                    logger.error("failed to load {} entries for {}", className, path.toString(), e);
                }
            });

            logger.debug("Loaded {} {} entries.", GameData.getMonsterConfigData().size(), className);
        } catch (IOException e) {
            logger.error("Failed to load {} folder.", className);
        }
    }
    private static <T extends ConfigEntityBase> void loadConfigData(Map<String,T> targetMap, String folderPath, Class<T> configClass) {
        val className = configClass.getName();
        try(val stream = Files.newDirectoryStream(getResourcePath(folderPath), "*.json")) {
            stream.forEach(path -> {
                try {
                    val name = path.getFileName().toString().replace(".json", "");
                    targetMap.put(name, JsonUtils.loadToClass(path, configClass));
                } catch (Exception e) {
                    logger.error("failed to load {} entries for {}", className, path.toString(), e);
                }
            });

            logger.debug("Loaded {} {} entries.", GameData.getMonsterConfigData().size(), className);
        } catch (IOException e) {
            logger.error("Failed to load {} folder.", className);
        }
    }

    private static void loadSceneRoutes() {
        try(val stream = Files.newDirectoryStream(getResourcePath("BinOutput/LevelDesign/Routes/"), "*.json")) {
            stream.forEach(path -> {
                try {
                    val sceneRoutes = JsonUtils.loadToClass(path, SceneRoutes.class);
                    val sceneRoutesMap = GameData.getSceneRoutes(sceneRoutes.getSceneId());
                    if(sceneRoutes.getRoutes() == null){
                        //logger.info("No routes found for scene {}", sceneRoutes.getSceneId());
                        return;
                    }
                    Arrays.stream(sceneRoutes.getRoutes()).forEach(r -> sceneRoutesMap.put(r.getLocalId(), r));
                } catch (Exception e) {
                    logger.error("failed to load scene routes for " + path.toString(), e);
                }
            });

            logger.debug("Loaded SceneRoutes for {} scenes.", GameData.getGadgetConfigData().size());
        } catch (IOException e) {
            logger.error("Failed to load SceneRoutes folder.");
        }
    }

    private static void loadScenePointArrays() {
        val pattern = Pattern.compile("scene([0-9]+)_point_array\\.json");
        try (val dirStream = Files.newDirectoryStream(getResourcePath("Scripts/Scene"))) {
            dirStream.forEach(dirPath -> {
                try (val stream = Files.newDirectoryStream(dirPath, "scene*_point_array.json")) {
                    stream.forEach(path -> {
                        val matcher = pattern.matcher(path.getFileName().toString());
                        if (!matcher.find()) return;
                        int sceneId = Integer.parseInt(matcher.group(1));

                        List<ScenePointArrayData> config;
                        try {
                            config = JsonUtils.loadToList(path, ScenePointArrayData.class);
                        } catch (Exception e) {
                            logger.error("Error loading point array file: {}", path, e);
                            return;
                        }

                        if (config == null) return;

                        config.forEach((pointArray) -> {
                            GameData.getScenePointArrayData().put(sceneId, config);
                        });

                    });
                } catch (IOException e) {
                    logger.error("Point array files cannot be found in {}", dirPath);
                }

            });
        } catch (IOException e) {
            logger.error("Point array files cannot be found");
        }
    }

    private static void loadBlossomResources() {
        try {
            GameDepot.setBlossomConfig(DataLoader.loadClass("BlossomConfig.json", BlossomConfig.class));
            logger.debug("Loaded BlossomConfig.");
        } catch (IOException e) {
            logger.warn("Failed to load BlossomConfig.");
        }
    }

    private static void loadConfigLevelEntityData(){
        // Load from BinOutput
        val pattern = Pattern.compile("ConfigLevelEntity_(.+?)\\.json");

        try(val stream = Files.newDirectoryStream(getResourcePath("BinOutput/LevelEntity/"), "ConfigLevelEntity_*.json")) {
            stream.forEach(path -> {
                val matcher = pattern.matcher(path.getFileName().toString());
                if (!matcher.find()) return;
                Map<String, ConfigLevelEntity> config;

                try {
                    config = JsonUtils.loadToMap(path, String.class, ConfigLevelEntity.class);
                } catch (Exception e) {
                    logger.error("Error loading player ability embryos:", e);
                    return;
                }
                GameData.getConfigLevelEntityDataMap().putAll(config);
            });
        } catch (IOException e) {
            logger.error("Error loading config level entity: no files found");
            return;
        }

        if (GameData.getConfigLevelEntityDataMap() == null || GameData.getConfigLevelEntityDataMap().isEmpty()) {
            logger.error("No config level entity loaded!");
        }
    }
    private static void loadScriptData(){
        loadQuestShareConfig();
        loadGroupReplacements();
    }

    private static void loadQuestShareConfig(){
        // Load from Shared Quest scripts
        val pattern = Pattern.compile("Q(.+?)\\ShareConfig.lua");

        try(val stream = Files.newDirectoryStream(getResourcePath("Scripts/Quest/Share/"), "Q*ShareConfig.lua")) {
            stream.forEach(path -> {
                val matcher = pattern.matcher(path.getFileName().toString());
                if (!matcher.find()) return;
                val questId = Integer.parseInt(matcher.group(1));

                val sharedQuestParams = new ShardQuestScriptLoadParams(questId);
                if(!ScriptSystem.getScriptLoader().loadData(sharedQuestParams, script -> {
                    // these are Map<String, class>
                    val teleportDataMap = script.getGlobalVariableMap("quest_data", QuestData.class);
                    val rewindDataMap = script.getGlobalVariableMap("rewind_data", RewindData.class);

                    // convert them to Map<Integer, class> and cache
                    GameData.getTeleportDataMap().putAll(teleportDataMap.entrySet().stream().collect(Collectors.toMap(entry -> Integer.valueOf(entry.getKey()), Entry::getValue)));
                    GameData.getRewindDataMap().putAll(rewindDataMap.entrySet().stream().collect(Collectors.toMap(entry -> Integer.valueOf(entry.getKey()), Entry::getValue)));
                })) {
                    logger.error("Error while loading Quest Share Config: {}", path.getFileName().toString());
                }
            });
        } catch (IOException e) {
            logger.error("Error loading Quest Share Config: no files found");
            return;
        }
        if (GameData.getTeleportDataMap() == null || GameData.getTeleportDataMap().isEmpty()
            || GameData.getRewindDataMap() == null || GameData.getRewindDataMap().isEmpty()) {
            logger.error("No Quest Share Config loaded!");
        }
    }

    private static void loadGadgetMappings() {
        try {
            val gadgetMap = GameData.getGadgetMappingMap();
            try {
                JsonUtils.loadToList(getResourcePath("Server/GadgetMapping.json"), GadgetMapping.class).forEach(entry -> gadgetMap.put(entry.getGadgetId(), entry));;
            } catch (IOException | NullPointerException ignored) {}
            logger.debug("Loaded {} gadget mappings.", gadgetMap.size());
        } catch (Exception e) {
            logger.error("Unable to load gadget mappings.", e);
        }
    }

    private static void loadSubfieldMappings() {
        try {
            val subfieldMap = GameData.getSubfieldMappingMap();
            try {
                JsonUtils.loadToList(getResourcePath("Server/SubfieldMapping.json"), SubfieldMapping.class).forEach(entry -> subfieldMap.put(entry.getEntityId(), entry));;
            } catch (IOException | NullPointerException ignored) {}
            logger.debug("Loaded {} subfield mappings.", subfieldMap.size());
        } catch (Exception e) {
            logger.error("Unable to load subfield mappings.", e);
        }

        try {
            val dropSubfieldMap = GameData.getDropSubfieldMappingMap();
            try {
                JsonUtils.loadToList(getResourcePath("Server/DropSubfieldMapping.json"), DropSubfieldMapping.class).forEach(entry -> dropSubfieldMap.put(entry.getDropId(), entry));;
            } catch (IOException | NullPointerException ignored) {}
            logger.debug("Loaded {} drop subfield mappings.", dropSubfieldMap.size());
        } catch (Exception e) {
            logger.error("Unable to load drop subfield mappings.", e);
        }

        try {
            val dropTableExcelConfigDataMap = GameData.getDropTableExcelConfigDataMap();
            try {
                JsonUtils.loadToList(getResourcePath("Server/DropTableExcelConfigData.json"), DropTableExcelConfigData.class).forEach(entry -> dropTableExcelConfigDataMap.put(entry.getId(), entry));;
            } catch (IOException | NullPointerException ignored) {}
            logger.debug("Loaded {} drop table configs.", dropTableExcelConfigDataMap.size());
        } catch (Exception e) {
            logger.error("Unable to load drop table config data.", e);
        }
    }

    private static void loadWeatherMappings() {
        try {
            val weatherMap = GameData.getWeatherMappingMap();
            try {
                JsonUtils.loadToList(getResourcePath("Server/WeatherMapping.json"), WeatherMapping.class).forEach(entry -> weatherMap.put(entry.getAreaId(), entry));;
            } catch (IOException | NullPointerException ignored) {}
            logger.debug("Loaded {} weather mappings.", weatherMap.size());
        } catch (Exception e) {
            logger.error("Unable to load weather mappings.", e);
        }
    }

    private static void loadMonsterMappings() {
        try {
            val monsterMap = GameData.getMonsterMappingMap();
            try {
                JsonUtils.loadToList(getResourcePath("Server/MonsterMapping.json"), MonsterMapping.class).forEach(entry -> monsterMap.put(entry.getMonsterId(), entry));;
            } catch (IOException | NullPointerException ignored) {}
            logger.debug("Loaded {} monster mappings.", monsterMap.size());
        } catch (Exception e) {
            logger.error("Unable to load monster mappings.", e);
        }
    }

    private static void loadActivityCondGroups() {
        try {
            val gadgetMap = GameData.getActivityCondGroupMap();
            try {
                JsonUtils.loadToList(getResourcePath("Server/ActivityCondGroups.json"), ActivityCondGroup.class).forEach(entry -> gadgetMap.put(entry.getCondGroupId(), entry));
            } catch (IOException | NullPointerException ignored) {}
            logger.debug("Loaded {} ActivityCondGroups.", gadgetMap.size());
        } catch (Exception e) {
            logger.error("Unable to load ActivityCondGroups.", e);
        }
    }

    private static void loadTrialAvatarCustomData() {
        try {
            String pathName = "TrialAvatar/";
            try {
                JsonUtils.loadToList(
                    getResourcePath(pathName + "TrialAvatarActivityDataExcelConfigData.json"),
                    TrialAvatarActivityDataData.class).forEach(instance -> {
                        instance.onLoad();
                        GameData.getTrialAvatarActivityDataCustomData()
                            .put(instance.getTrialAvatarIndexId(), instance);
                    });
            } catch (IOException | NullPointerException ignored) {}
            logger.debug("Loaded trial activity custom data.");
            try {
                JsonUtils.loadToList(
                    getResourcePath(pathName + "TrialAvatarActivityExcelConfigData.json"),
                    TrialAvatarActivityCustomData.class).forEach(instance -> {
                        instance.onLoad();
                        GameData.getTrialAvatarActivityCustomData()
                            .put(instance.getScheduleId(), instance);
                    });
            } catch (IOException | NullPointerException ignored) {}
            logger.debug("Loaded trial activity schedule custom data.");
            try {
                JsonUtils.loadToList(
                    getResourcePath(pathName + "TrialAvatarCustomConfigData.json"),
                    TrialAvatarCustomData.class).forEach(instance -> {
                        GameData.getTrialAvatarCustomData()
                            .put(instance.getTrialAvatarId(), instance);
                    });
            } catch (IOException | NullPointerException ignored) {}
            logger.debug("Loaded trial avatar custom data.");
        } catch (Exception e) {
            logger.error("Unable to load trial avatar custom data.", e);
        }
    }

    private static void loadGroupReplacements(){
        val scriptParams = new SceneReplacementScriptLoadParams();
        if(!ScriptSystem.getScriptLoader().loadData(scriptParams, script -> {
            // these are Map<String, class>
            var replacementsMap = script.getGlobalVariableMap("replacements", SceneGroupReplacement.class);
            // convert them to Map<Integer, class> and cache
            GameData.getGroupReplacements().putAll(replacementsMap.entrySet().stream().collect(Collectors.toMap(entry -> Integer.valueOf(entry.getValue().getId()), Entry::getValue)));
        })) {
            logger.error("Error while loading Group Replacements");
        }
    }

    // BinOutput configs

    public static class AbilityGroup {
        String abilityGroupSourceType; // todo probably enum?
        String abilityGroupTargetType; // todo probably enum?

        @SerializedName(value="abilities", alternate={"targetAbilities"})
        public List<AvatarConfigAbility> targetAbilities;
    }

    public static class AvatarConfigAbility {
        public String abilityName;
        public String toString() {
            return abilityName;
        }
    }

    public static class OpenConfigData {
        public String $type;
        public String abilityName;

        @SerializedName(value="talentIndex", alternate={"OJOFFKLNAHN"})
        public int talentIndex;

        @SerializedName(value="skillID", alternate={"overtime"})
        public int skillID;

        @SerializedName(value="pointDelta", alternate={"IGEBKIHPOIF"})
        public int pointDelta;
    }
}
