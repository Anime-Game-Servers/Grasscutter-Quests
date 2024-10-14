package emu.grasscutter.game.world;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.DungeonData;
import emu.grasscutter.data.excels.SceneData;
import emu.grasscutter.game.entity.EntityTeam;
import emu.grasscutter.game.entity.EntityWorld;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.player.Player.SceneLoadState;
import emu.grasscutter.game.props.EnterReason;
import emu.grasscutter.game.props.EntityIdType;
import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.game.props.SceneType;
import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.game.world.data.TeleportProperties;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.server.event.player.PlayerTeleportEvent;
import emu.grasscutter.server.event.player.PlayerTeleportEvent.TeleportType;
import emu.grasscutter.server.game.GameServer;
import emu.grasscutter.server.packet.send.*;
import emu.grasscutter.utils.Position;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.*;
import org.anime_game_servers.multi_proto.gi.messages.scene.EnterType;

import java.util.*;
import java.util.stream.Collectors;

import static emu.grasscutter.server.event.player.PlayerTeleportEvent.TeleportType.SCRIPT;

public class World implements Iterable<Player> {
    @Getter private final GameServer server;
    @Getter private final Player owner;
    @Getter private final List<Player> players;
    @Getter private final Int2ObjectMap<Scene> scenes;

    @Getter private EntityWorld entity;
    private int nextEntityId = 0;
    private int nextPeerId = 0;
    @Getter private int worldLevel;

    @Getter private boolean isMultiplayer;


    @Getter private int tickCount = 0;
    @Getter private boolean isPaused = false;
    @Getter private boolean isGameTimeLocked = false;
    @Getter private boolean isWeatherLocked = false;
    private long lastUpdateTime;
    @Getter private long currentWorldTime = 0;
    @Getter private long currentGameTime = 540;

    @Getter private Random worldRandomGenerator;

    public World(Player player) {
        this(player, false);
    }

    public World(Player player, boolean isMultiplayer) {
        this.owner = player;
        this.server = player.getServer();
        this.players = Collections.synchronizedList(new ArrayList<>());
        this.scenes = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());

        //this.levelEntityId = this.getNextEntityId(EntityIdType.MPLEVEL);
        this.entity = new EntityWorld(this);
        this.worldLevel = player.getWorldLevel();
        this.isMultiplayer = isMultiplayer;
        this.lastUpdateTime = System.currentTimeMillis();
        this.currentGameTime = owner.getPlayerGameTime();
        this.isGameTimeLocked = owner.getBoolProperty(PlayerProperty.PROP_IS_GAME_TIME_LOCKED);
        this.isWeatherLocked = owner.getBoolProperty(PlayerProperty.PROP_IS_WEATHER_LOCKED);

        this.worldRandomGenerator = new Random();

        this.owner.getServer().registerWorld(this);
    }

    public boolean setGameTimeLocked(boolean gameTimeLocked) {
        if(isMultiplayer()){
            // todo maybe build logic to overwrite a property temporarily for multiplayer?
            return false;
        }
        isGameTimeLocked = gameTimeLocked;
        getPlayers().forEach(p -> p.setProperty(PlayerProperty.PROP_IS_GAME_TIME_LOCKED, gameTimeLocked));
        return true;
    }

    public boolean setWeatherIsLocked(boolean weatherLocked) {
        isWeatherLocked = weatherLocked;
        getPlayers().forEach(p -> p.setProperty(PlayerProperty.PROP_IS_WEATHER_LOCKED, isWeatherLocked));
        return true;
    }

    public Player getHost() {
        return owner;
    }


    public int getLevelEntityId() {
        return entity.getId();
    }

    public int getHostPeerId() {
        if (this.getHost() == null) {
            return 0;
        }
        return this.getHost().getPeerId();
    }

    public int getNextPeerId() {
        return ++this.nextPeerId;
    }

    public void setWorldLevel(int worldLevel) {
        this.worldLevel = worldLevel;
    }

    public Scene getSceneById(int sceneId) {
        // Get scene normally
        Scene scene = this.getScenes().get(sceneId);
        if (scene != null) {
            return scene;
        }

        // Create scene from scene data if it doesn't exist
        SceneData sceneData = GameData.getSceneDataMap().get(sceneId);
        if (sceneData != null) {
            scene = new Scene(this, sceneData);
            this.registerScene(scene);
            return scene;
        }

        return null;
    }

    public int getPlayerCount() {
        return this.getPlayers().size();
    }

    public int getNextEntityId(EntityIdType idType) {
        return idType.toTypedEntityId(++this.nextEntityId);
    }

    public synchronized void addPlayer(Player player) {
        // Check if player already in
        if (this.getPlayers().contains(player)) {
            return;
        }

        // Remove player from prev world
        if (player.getWorld() != null) {
            player.getWorld().removePlayer(player);
        }

        // Register
        player.setWorld(this);
        this.getPlayers().add(player);

        // Set player variables
        player.setPeerId(this.getNextPeerId());
        player.getTeamManager().setEntity(new EntityTeam(player));
        //player.getTeamManager().setEntityId(this.getNextEntityId(EntityIdType.TEAM));

        // Copy main team to multiplayer team
        if (this.isMultiplayer()) {
            player.getTeamManager().getMpTeam().copyFrom(player.getTeamManager().getCurrentSinglePlayerTeamInfo(), player.getTeamManager().getMaxTeamSize());
            player.getTeamManager().setCurrentCharacterIndex(0);
        }

        // Add to scene
        Scene scene = this.getSceneById(player.getSceneId());
        scene.addPlayer(player);

        // Info packet for other players
        if (this.getPlayers().size() > 1) {
            this.updatePlayerInfos(player);
        }
    }

    public synchronized void removePlayer(Player player) {
        // Remove team entities
        player.sendPacket(
                new PacketDelTeamEntityNotify(
                        player.getSceneId(),
                    this.getPlayers().stream().map(p -> p.getTeamManager().getEntity() == null ? 0 : p.getTeamManager().getEntity().getId()).collect(Collectors.toList())
                )
        );

        // Deregister
        this.getPlayers().remove(player);
        player.setWorld(null);

        // Remove from scene
        Scene scene = this.getSceneById(player.getSceneId());
        scene.removePlayer(player);

        // Info packet for other players
        if (this.getPlayers().size() > 0) {
            this.updatePlayerInfos(player);
        }

        // Disband world if host leaves
        if (this.getHost() == player) {
            List<Player> kicked = new ArrayList<>(this.getPlayers());
            for (Player victim : kicked) {
                World world = new World(victim);
                world.addPlayer(victim);

                victim.sendPacket(new PacketPlayerEnterSceneNotify(victim, EnterType.ENTER_SELF, EnterReason.TeamKick, victim.getSceneId(), victim.getPosition()));
            }
        }
    }

    public void registerScene(Scene scene) {
        this.getScenes().put(scene.getId(), scene);
    }

    public void deregisterScene(Scene scene) {
        scene.saveGroups();
        this.getScenes().remove(scene.getId());
    }

    public void save() {
        this.getScenes().values().forEach(Scene::saveGroups);
    }

    public boolean transferPlayerToScene(Player player, int sceneId, Position pos, Position rot) {
        return this.transferPlayerToScene(player, sceneId, TeleportType.INTERNAL, null, pos, rot);
    }

    public boolean transferPlayerToScene(Player player, int sceneId, TeleportType teleportType, Position pos, Position rot) {
        return this.transferPlayerToScene(player, sceneId, teleportType, null, pos, rot);
    }

    public boolean transferPlayerToScene(Player player, int sceneId, DungeonData data) {
        return this.transferPlayerToScene(player, sceneId, TeleportType.DUNGEON, data, null, null);
    }

    public boolean transferPlayerToScene(Player player, int sceneId, TeleportType teleportType, DungeonData dungeonData, Position teleportTo, Position newRot) {
        EnterReason enterReason = switch (teleportType) {
            // shouldn't affect the teleportation, but its clearer when inspecting the packets
            // TODO add more conditions for different reason.
            case INTERNAL, WAYPOINT, MAP -> EnterReason.TransPoint;
            case COMMAND -> EnterReason.Gm;
            case SCRIPT -> EnterReason.Lua;
            case CLIENT -> EnterReason.ClientTransmit;
            case DUNGEON -> EnterReason.DungeonEnter;
            default -> EnterReason.None;
        };
        return transferPlayerToScene(player, sceneId, teleportType, enterReason, dungeonData, teleportTo, newRot);
    }


    public boolean transferPlayerToScene(Player player, int sceneId, TeleportType teleportType, EnterReason enterReason, DungeonData dungeonData, Position teleportTo, Position newRot) {
        // Get enter types
        val teleportProps = TeleportProperties.builder()
            .sceneId(sceneId)
            .teleportType(teleportType)
            .enterReason(enterReason)
            .teleportTo(teleportTo)
            .teleportRot(newRot)
            .enterType(EnterType.ENTER_JUMP)
            .dungeonId(Optional.ofNullable(dungeonData).map(DungeonData::getId).orElse(0))
            .prevPos(player.getPosition())
            .prevSceneId(player.getSceneId())
            .worldType(Optional.ofNullable(dungeonData).map(data -> 13).orElse(14)); // TODO find out more

        val sceneData = GameData.getSceneDataMap().get(sceneId);
        if (dungeonData != null) {
            teleportProps.enterType(EnterType.ENTER_DUNGEON)
                .enterReason(EnterReason.DungeonEnter);
        } else if (player.getSceneId() == sceneId) {
            teleportProps.enterType(EnterType.ENTER_GOTO);
        } else if (sceneData!= null && sceneData.getSceneType() == SceneType.SCENE_HOME_WORLD) {
            // Home
            teleportProps.enterType(EnterType.ENTER_SELF_HOME)
                .enterReason(EnterReason.EnterHome);
        }
        return transferPlayerToScene(player, teleportProps.build());
    }

    public boolean transferPlayerToScene(Player player, TeleportProperties teleportProperties) {
        // Call player teleport event.
        PlayerTeleportEvent event = new PlayerTeleportEvent(player, teleportProperties, player.getPosition());
        // Call event & check if it was canceled.
        event.call(); if (event.isCanceled()) {
            return false; // Teleport was canceled.
        }

        if (GameData.getSceneDataMap().get(teleportProperties.getSceneId()) == null) {
            return false;
        }

        Scene oldScene = null;

        if (player.getScene() != null) {
            oldScene = player.getScene();

            // Don't deregister scenes if the player is going to tp back into them
            if (oldScene.getId() == teleportProperties.getSceneId()) {
                oldScene.setDontDestroyWhenEmpty(true);
            }

            oldScene.removePlayer(player);
        }

        Scene newScene = this.getSceneById(teleportProperties.getSceneId());
        newScene.addPlayer(player);
        player.setAvatarsAbilityForScene(newScene);
        // Dungeon
        // Dungeon system is handling this already
        // if(dungeonData!=null){
        //     var dungeonManager = new DungeonManager(newScene, dungeonData);
        //     dungeonManager.startDungeon();
        // }
        val config = newScene.getScriptManager().getConfig();
        if (teleportProperties.getTeleportTo() == null && config != null) {
            Optional.ofNullable(config.getBornPos()).map(Position::new).ifPresent(teleportProperties::setTeleportTo);
            Optional.ofNullable(config.getBornRot()).map(Position::new).ifPresent(teleportProperties::setTeleportRot);
        }

        // Set player position and rotation
        Optional.ofNullable(teleportProperties.getTeleportTo()).ifPresent(player.getPosition()::set);
        Optional.ofNullable(teleportProperties.getTeleportRot()).ifPresent(player.getRotation()::set);

        if (oldScene != null && newScene != oldScene) {
            newScene.setPrevScene(oldScene.getId());
            oldScene.setDontDestroyWhenEmpty(false);
        }


        // Teleport packet
        player.sendPacket(new PacketPlayerEnterSceneNotify(player, teleportProperties));
        player.updateWeather(newScene);

        if(teleportProperties.getTeleportType() != TeleportType.INTERNAL && teleportProperties.getTeleportType() != SCRIPT) {
            player.getQuestManager().queueEvent(QuestContent.QUEST_CONTENT_ANY_MANUAL_TRANSPORT);
        }
        return true;
    }

    private void updatePlayerInfos(Player paramPlayer) {
        for (Player player : this.getPlayers()) {
            // Don't send packets if player is logging in and filter out joining player
            if (!player.hasSentLoginPackets() || player == paramPlayer) {
                continue;
            }

            // Update team of all players since max players has been changed - Probably not the best way to do it
            if (this.isMultiplayer()) {
                player.getTeamManager().getMpTeam().copyFrom(player.getTeamManager().getMpTeam(), player.getTeamManager().getMaxTeamSize());
                player.getTeamManager().updateTeamEntities(true);
            }

            // Don't send packets if player is loading into the scene
            if (player.getSceneLoadState().getValue() < SceneLoadState.INIT.getValue() ) {
                // World player info packets
                player.getSession().send(new PacketWorldPlayerInfoNotify(this));
                player.getSession().send(new PacketScenePlayerInfoNotify(this));
                player.getSession().send(new PacketWorldPlayerRTTNotify(this));

                // Team packets
                player.getSession().send(new PacketSyncTeamEntityNotify(player));
                player.getSession().send(new PacketSyncScenePlayTeamEntityNotify(player));
            }
        }
    }

    public void broadcastPacket(BasePacket packet) {
        // Send to all players - might have to check if player has been sent data packets
        for (Player player : this.getPlayers()) {
            player.getSession().send(packet);
        }
    }

    // Returns true if the world should be deleted
    public boolean onTick() {
        if (this.getPlayerCount() == 0) return true;
        this.scenes.values().stream()
            .filter(scene -> scene.getPlayerCount() > 0)
            .forEach(Scene::onTick);

        if(!isGameTimeLocked && !isPaused){
            currentGameTime++;
        }


        // sync time every 10 seconds
        if(tickCount%10 == 0){
            players.forEach(p -> p.sendPacket(new PacketPlayerGameTimeNotify(p)));
            isGameTimeLocked = getHost().getBoolProperty(PlayerProperty.PROP_IS_GAME_TIME_LOCKED);
            isWeatherLocked = getHost().getBoolProperty(PlayerProperty.PROP_IS_WEATHER_LOCKED);
        }
        // store updated world time every 60 seconds (ingame hour)
        if(tickCount%60 == 0){
            this.owner.updatePlayerGameTime(currentGameTime);
        }
        tickCount++;
        return false;
    }

    public void close() {

    }

    public boolean changeTime(int targetTime, boolean forced){
        if(!forced && isGameTimeLocked){
            return false;
        }
        this.currentGameTime = targetTime;
        this.owner.updatePlayerGameTime(currentGameTime);
        this.players.forEach(player -> player.getQuestManager().queueEvent(QuestContent.QUEST_CONTENT_GAME_TIME_TICK,
            getGameTimeHours()));
        return true;
    }

    public boolean changeTime(int time, int days, boolean forced) {
        if(!forced && isGameTimeLocked){
            return false;
        }
        val currentTime = getGameTime();
        var diff = time - currentTime;
        if(diff < 0){
            diff = 1440 + diff;
        }
        this.currentGameTime += days * 1440L + diff;
        this.owner.updatePlayerGameTime(currentGameTime);
        this.players.forEach(player -> player.getQuestManager().queueEvent(QuestContent.QUEST_CONTENT_GAME_TIME_TICK,
            getGameTimeHours(), // hours
            days)); //days
        return true;
    }

    public void setPaused(boolean paused) {
        getWorldTime();
        if(this.isPaused != paused && !paused){
            this.lastUpdateTime = System.currentTimeMillis();
        }
        isPaused = paused;
        players.forEach(player -> player.setPaused(paused));
        scenes.forEach((key, scene) -> scene.setPaused(paused));
    }

    public static long getDaysForGameTime(long inGameMinutes){
        return inGameMinutes / 1440;
    }

    public static long getHoursForGameTime(long inGameMinutes){
        return inGameMinutes / 60;
    }

    /**
     * Returns the current in game days world time in ingame minutes (0-1439)
     */
    public int getGameTime() {
        return (int)(currentGameTime % 1440);
    }

    /**
     * Returns the current in game days world time in ingame hours (0-23)
     */
    public int getGameTimeHours() {
        return getGameTime() / 60 ;
    }

    /**
     * Returns the total number of in game days that got completed since the beginning of the game
     */
    public long getTotalGameTimeDays() {
        return getDaysForGameTime(getTotalGameTimeMinutes());
    }

    /**
     * Returns the total number of in game hours that got completed since the beginning of the game
     */
    public long getTotalGameTimeHours() {
        return getHoursForGameTime(getTotalGameTimeMinutes());
    }

    /**
     * Returns the total amount of ingame minutes that got completed since the beginning of the game
     */
    public long getTotalGameTimeMinutes() {
        return currentGameTime;
    }

    /**
     * Returns the ingame world time in irl millis
     */
    public long getWorldTime() {
        if(!isPaused) {
            long newUpdateTime = System.currentTimeMillis();
            this.currentWorldTime += (newUpdateTime - lastUpdateTime);
            this.lastUpdateTime = newUpdateTime;
        }
        return currentWorldTime;
    }

    @Override
    public Iterator<Player> iterator() {
        return this.getPlayers().iterator();
    }
}
