package emu.grasscutter.game.player;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Transient;
import emu.grasscutter.GameConstants;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.AvatarSkillDepotData;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.avatar.AvatarStorage;
import emu.grasscutter.game.avatar.TrialAvatar;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.entity.EntityBaseGadget;
import emu.grasscutter.game.entity.EntityTeam;
import emu.grasscutter.game.props.ElementType;
import emu.grasscutter.game.props.EnterReason;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.game.world.World;
import emu.grasscutter.server.event.player.PlayerTeamDeathEvent;
import emu.grasscutter.server.packet.send.*;
import emu.grasscutter.utils.Position;
import emu.grasscutter.utils.Utils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.PlayerDieType;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.scene.EnterType;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.MotionState;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.IntStream;

import static emu.grasscutter.config.Configuration.GAME_OPTIONS;

@Entity @Getter
public class TeamManager extends BasePlayerDataManager {
    // This needs to be a LinkedHashMap to guarantee insertion order.
    @Setter private LinkedHashMap<Integer, TeamInfo> teams;
    @Setter private int currentTeamId;
    private int currentCharacterIndex;

    @Transient @Setter private EntityTeam entity;
    @Transient private final List<EntityAvatar> activeTeam;
    @Transient private final Set<EntityBaseGadget> gadgets;
    @Transient private final IntSet teamResonances;
    @Transient private final IntSet teamResonancesConfig;
    @Transient @Setter private TeamInfo mpTeam;
    @Transient @Setter private int temporaryTeamIndex = -1;
    @Transient private final List<TeamInfo> temporaryTeam; // Temporary Team for tower
    @Transient @Setter private boolean useTrialTeam;
    @Transient @Setter private TeamInfo trialAvatarTeam;
    // used to hold temp team's avatar guid, trial avatar, tower team etc
    @Transient private final LinkedHashMap<Integer, Long> entityGuids;
    @Transient @Setter private int previousIndex = -1; // index of character selection in team before adding trial avatar

    public TeamManager() {
        this.mpTeam = new TeamInfo();
        this.activeTeam = new ArrayList<>();
        this.gadgets = new HashSet<>();
        this.teamResonances = new IntOpenHashSet();
        this.teamResonancesConfig = new IntOpenHashSet();
        this.temporaryTeam = new ArrayList<>();
        this.trialAvatarTeam = new TeamInfo();
        this.entityGuids = new LinkedHashMap<>();
        if(currentTeamId == 0) currentTeamId = 1;
    }

    public TeamManager(Player player) {
        this();
        setPlayer(player);

        this.setCurrentTeamId(1);
        this.setTeams(new LinkedHashMap<>());
        IntStream.rangeClosed(1, GameConstants.DEFAULT_TEAMS)
            .forEachOrdered(i -> this.getTeams().put(i, new TeamInfo()));
    }

    public World getWorld() {
        return getPlayer().getWorld();
    }

    public long getCurrentCharacterGuid() {
        return getCurrentAvatarEntity().getAvatar().getGuid();
    }

    public TeamInfo getCurrentTeamInfo() {
        if (isUseTrialTeam()) return getTrialAvatarTeam();
        if (getTemporaryTeamIndex() >= 0 &&
            getTemporaryTeamIndex() < getTemporaryTeam().size()) {
            return getTemporaryTeam().get(getTemporaryTeamIndex());
        }
        if (getPlayer().isInMultiplayer()) {
            return getMpTeam();
        }

        return getCurrentSinglePlayerTeamInfo();
    }

    public TeamInfo getCurrentSinglePlayerTeamInfo() {
        val team = getTeams().get(getCurrentTeamId());
        if (team == null) {
            val first = getTeams().entrySet().stream().findFirst();
            if (first.isPresent()) {
                setCurrentTeamId(first.get().getKey());
                return first.get().getValue();
            }
        }
        return team;
    }

    public void setCurrentCharacterIndex(int index) {
        // clip index within [0, active team size)
        this.currentCharacterIndex = Math.min(Math.max(index, 0), getActiveTeam().size()-1);
    }

    public EntityAvatar getCurrentAvatarEntity() {
        if(getActiveTeam().isEmpty()) {
            val mainChar = player.getAvatars().getAvatars().get(player.getMainCharacterId());
            if(mainChar == null) return null;
            addAvatarToCurrentTeam(mainChar);
        }

        if (getActiveTeam().isEmpty() || getCurrentCharacterIndex() < 0) return null;

        if(getCurrentCharacterIndex() >= getActiveTeam().size()) return getActiveTeam().get(0);

        return getActiveTeam().get(getCurrentCharacterIndex());
    }

    public int getMaxTeamSize() {
        if (getPlayer().isInMultiplayer()) {
            int teamSize = GAME_OPTIONS.avatarLimits.multiplayerTeam / getWorld().getPlayerCount();

            return Math.max(1, getPlayer().getWorld().getHost() == getPlayer() ?
                (int) Math.floor(teamSize) : (int) Math.ceil(teamSize));
        }

        return GAME_OPTIONS.avatarLimits.singlePlayerTeam;
    }

    // Methods

    /**
     * Returns true if there is space to add the multiple avatars to the team.
     */
    public boolean canAddAvatarsToTeam(@NotNull TeamInfo team, int avatars) {
        return team.size() + avatars <= getMaxTeamSize();
    }

    /**
     * Returns true if there is space to add to the current team.
     * If the current team is temporary, returns false.
     */
    public boolean canAddAvatarToCurrentTeam() {
        return canAddAvatarsToTeam(getCurrentTeamInfo(), 1);
    }

    /**
     * Try to add the collection of avatars to the team.
     * Returns true if all were successfully added.
     * If some can not be added, returns false and does not add any.
     */
    public void addAvatarToCurrentTeam(Avatar avatar) {
        if (!canAddAvatarToCurrentTeam()) return;

        // Convert avatars into a collection of avatar IDs, then add
        getCurrentTeamInfo().getAvatars().add(avatar.getAvatarId());

        // SP team update packet
        getPlayer().sendPacket(new PacketAvatarTeamUpdateNotify(getPlayer()));

        // Update entities
        updateTeamEntities(true);
    }

    private void updateTeamResonances() {
        getTeamResonances().clear();
        getTeamResonancesConfig().clear();
        // Official resonances require a full party
        if (getActiveTeam().size() < 4) return;

        // TODO: make this actually read from TeamResonanceExcelConfigData.json for the real resonances and conditions
        // Currently we just hardcode these conditions, but this won't work for modded resources or future changes
        var elementCounts = new Object2IntOpenHashMap<ElementType>();
        getActiveTeam().stream()
            .map(EntityAvatar::getAvatar).filter(Objects::nonNull)
            .map(Avatar::getSkillDepot).filter(Objects::nonNull)
            .map(AvatarSkillDepotData::getElementType).filter(Objects::nonNull)
            .forEach(elementType -> elementCounts.addTo(elementType, 1));

        // Dual element resonances
        elementCounts.object2IntEntrySet().stream()
            .filter(e -> e.getIntValue() >= 2)
            .map(Map.Entry::getKey)
            .filter(elementType -> elementType.getTeamResonanceId() != 0)
            .forEach(elementType -> {
                getTeamResonances().add(elementType.getTeamResonanceId());
                getTeamResonancesConfig().add(elementType.getConfigHash());
            });

        // Four element resonance
        if (elementCounts.size() >= 4) {
            getTeamResonances().add(ElementType.Default.getTeamResonanceId());
            getTeamResonancesConfig().add(ElementType.Default.getConfigHash());
        }
    }

    private long getAvatarGuid(AvatarStorage storage, int avatarId){
        Long avatarGuid = getEntityGuids().get(avatarId);
        if(avatarGuid == null){
            val fallbackAvatar = storage.getAvatarById(avatarId);
            avatarGuid = fallbackAvatar != null ? fallbackAvatar.getGuid() : -1;
        }
        return avatarGuid;
    }

    /**
     * Updates the active team of the player based on the current team information.
     * Removes any entities that are not in the new team and adds any new entities.
     * Also updates the selected character index and team properties.
     */
    public synchronized void updateTeamEntities(boolean shouldReplace) {
        val team = getCurrentTeamInfo();
        val avatars = team.getAvatars();
        // Sanity check - Should never happen
        if (avatars.isEmpty()) return;

        // If current team has changed
        EntityAvatar currentEntity = getCurrentAvatarEntity();
        Long2ObjectMap<EntityAvatar> existingAvatars = new Long2ObjectOpenHashMap<>();

        getActiveTeam().forEach(entity -> existingAvatars.put(entity.getAvatar().getGuid(), entity));

        // Clear active team entity list
        getActiveTeam().clear();

        // Add back entities into team
        val avatarStorage = getPlayer().getAvatars();
        avatars.forEach(avatarId -> {
            val avatarGuid = getAvatarGuid(avatarStorage, avatarId);

            getActiveTeam().add(existingAvatars.containsKey(avatarGuid) ?
                existingAvatars.remove(avatarGuid) :
                new EntityAvatar(getPlayer().getScene(), avatarStorage.getAvatarByGuid(avatarGuid)));
        });

        // Set new selected character index
        int prevSelectedIndex;
        if (getEntityGuids().isEmpty()) {
            prevSelectedIndex = getPreviousIndex() > 0 ? getPreviousIndex() : getActiveTeam().indexOf(currentEntity);
        } else {
            // it means that there is trial avatar and the first of multiple (if any) of them
            // will be used as index
            prevSelectedIndex = getCurrentTeamInfo().getAvatars().indexOf(
                getEntityGuids().keySet().iterator().next()
            );
        }

        setCurrentCharacterIndex(prevSelectedIndex < 0 ? getCurrentCharacterIndex() : prevSelectedIndex);

        // Update team resonances
        updateTeamResonances();

        // Packets
        getPlayer().sendPacket(new PacketSceneTeamUpdateNotify(getPlayer()));

        // Skill charges packet - Yes, this is official server behavior as of 2.6.0
        getActiveTeam().stream().map(EntityAvatar::getAvatar).forEach(Avatar::sendSkillExtraChargeMap);
        saveAvatars(existingAvatars.values());
        if (getPlayer().getScene() == null) return;

        existingAvatars.values().forEach(entity -> getPlayer().getScene().removeEntity(entity));
        if (!shouldReplace || currentEntity == getCurrentAvatarEntity()) return; // Check if character changed

        if (currentEntity == null) {
            getPlayer().getScene().addEntity(getCurrentAvatarEntity());
        } else {
            // Remove and Add
            getPlayer().getScene().replaceEntity(currentEntity, getCurrentAvatarEntity());
        }
    }

    public List<Integer> newTeam(@NotNull Collection<Long> guidList) {
        return guidList.stream()
            .map(guid -> getPlayer().getAvatars().getAvatarByGuid(guid))
            .filter(Objects::nonNull)
            .map(Avatar::getAvatarId).toList();
    }

    public synchronized boolean setupAvatarTeam(int teamId, @NotNull List<Long> guidList, long guid) {
        // Sanity checks
        TeamInfo teamInfo = getTeams().get(teamId);
        if (guidList.isEmpty() || guidList.size() > getMaxTeamSize() || teamInfo == null) return false;

        // Set team data and clear current team info and add avatars from our new team
        teamInfo.getAvatars().clear();

        // Convert avatars into a collection of avatar IDs, then add
        teamInfo.getAvatars().addAll(newTeam(guidList));

        // Update team
        // SP team update packet
        getPlayer().sendPacket(new PacketAvatarTeamUpdateNotify(getPlayer()));

        // This is one of the player's teams
        // Update entities
        if (teamId == getCurrentTeamId()) {
            setCurrentCharacterIndex(guidList.indexOf(guid));
            updateTeamEntities(true);
        }
        return true;
    }

    public boolean setupMpTeam(@NotNull List<Long> guidList, long guid) {
        // Sanity checks
        TeamInfo teamInfo = getMpTeam();
        if (guidList.isEmpty() || guidList.size() > getMaxTeamSize() ||
            !canAddAvatarsToTeam(teamInfo, newTeam(guidList).size())) return false;

        // Set team data and clear current team info and add avatars from our new team
        teamInfo.getAvatars().clear();

        // Convert avatars into a collection of avatar IDs, then add
        teamInfo.getAvatars().addAll(newTeam(guidList));
        setCurrentCharacterIndex(guidList.indexOf(guid));

        // MP team Packet
        updateTeamEntities(true);
        return true;
    }

    public synchronized void setupTrialAvatarTeamForQuest() {
        getTrialAvatarTeam().copyFrom(getCurrentTeamInfo());
        setupTrialAvatarTeamForDungeon();
    }

    public synchronized void setupTrialAvatarTeamForDungeon() {
        setPreviousIndex(getCurrentCharacterIndex());
        setUseTrialTeam(true);
    }

    public void addAvatarToTrialTeam(TrialAvatar trialAvatar) {
        getCurrentTeamInfo().getAvatars().removeIf(x -> x == trialAvatar.getAvatarId());
        getCurrentTeamInfo().addAvatar(trialAvatar);
        getEntityGuids().put(trialAvatar.getAvatarId(), trialAvatar.getGuid());
        EntityAvatar trialEntity = new EntityAvatar(getPlayer().getScene(), trialAvatar);
        getActiveTeam().add(trialEntity);
        getPlayer().getScene().getEntities().put(trialEntity.getId(), trialEntity);
    }

    public synchronized void removeTrialAvatarTeam() {
        setUseTrialTeam(false);
        setTrialAvatarTeam(new TeamInfo());
        getEntityGuids().clear();
        updateTeamEntities(false);
        setPreviousIndex(-1);
    }

    public void setupTemporaryTeam(@NotNull List<List<Long>> guidListList) {
        this.temporaryTeam.clear();
        this.entityGuids.clear();
        // Sanity checks
        // Set team data and convert to avatar ids
        this.temporaryTeam.addAll(guidListList.stream().parallel()
            .filter(guidList -> !guidList.isEmpty()).filter(guidList -> guidList.size() <= getMaxTeamSize())
            .map(this::newTeam).filter(Objects::nonNull).filter(l -> !l.isEmpty())
            .map(TeamInfo::new)
            .toList());

        guidListList.stream().flatMap(List::stream).map(this.player.getAvatars()::getAvatarByGuid)
            .forEachOrdered(avatar ->  this.entityGuids.put(avatar.getAvatarId(), avatar.getGuid()));
    }

    public void useTemporaryTeam(int index) {
        this.temporaryTeamIndex = index;
        if (index == 0) {
            if (this.previousIndex < 0) this.previousIndex = this.currentCharacterIndex;
        } else {
            this.temporaryTeam.iterator().next().getAvatars().forEach(this.entityGuids::remove);
        }
        this.currentCharacterIndex = 0;
        updateTeamEntities(false);
    }

    public void cleanTemporaryTeam() {
        // check if using temporary team
        if (this.temporaryTeamIndex < 0) return;

        this.temporaryTeamIndex = -1;
        this.currentCharacterIndex = this.previousIndex;
        this.previousIndex = -1;
        this.temporaryTeam.clear();
        this.entityGuids.clear();
        updateTeamEntities(false);
    }

    public synchronized boolean setCurrentTeam(int teamId) {
        // Get team
        TeamInfo teamInfo = getTeams().get(teamId);
        if (getPlayer().isInMultiplayer() || teamInfo == null || teamInfo.getAvatars().isEmpty()) {
            return false;
        }

        // Set
        setCurrentTeamId(teamId);
        updateTeamEntities(true);
        return true;
    }

    public synchronized boolean setTeamName(int teamId, String teamName) {
        // Get team
        TeamInfo teamInfo = getTeams().get(teamId);
        if (teamInfo == null) return false;

        teamInfo.setName(teamName);
        return true;
    }

    public synchronized Retcode changeAvatar(long guid) {
        // TODO replace resulting retcode with appropriate ones
        EntityAvatar oldEntity = getCurrentAvatarEntity();

        if (guid == oldEntity.getAvatar().getGuid()) return Retcode.RET_FAIL;

        int index = IntStream.range(0, getActiveTeam().size())
            .filter(i -> guid == getActiveTeam().get(i).getAvatar().getGuid())
            .findFirst()
            .orElse(-1);

        EntityAvatar newEntity = (index == -1) ? null : getActiveTeam().get(index);

        if (index < 0 || newEntity == oldEntity) return Retcode.RET_FAIL;

        // Set index
        setCurrentCharacterIndex(index);

        // Old entity motion state
        oldEntity.setMotionState(MotionState.MOTION_STANDBY);

        // Remove and Add
        getPlayer().getScene().replaceEntity(oldEntity, newEntity);
        return Retcode.RET_SUCC;
    }

    public void onAvatarDieDamage(){
        getActiveTeam().forEach(entity -> {
            entity.damage(entity.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP) * .1f);
            player.sendPacket(new PacketAvatarLifeStateChangeNotify(entity.getAvatar()));
        });
    }

    public void onAvatarDie(long dieGuid) {
        EntityAvatar deadAvatar = getCurrentAvatarEntity();

        if (deadAvatar.isAlive() || deadAvatar.getId() != dieGuid) return;

        PlayerDieType dieType = deadAvatar.getKilledType();
        int killedBy = deadAvatar.getKilledBy();

        if (dieType == PlayerDieType.PLAYER_DIE_DRAWN) {
            // Died in water. Do not replace
            // The official server has skipped this notify and will just respawn the team immediately after the animation.
            // TODO: Perhaps find a way to get vanilla experience?
            getPlayer().sendPacket(new PacketWorldPlayerDieNotify(dieType, killedBy));
            return;
        }

        // Replacement avatar
        int replaceIndex = IntStream.range(0, getActiveTeam().size())
            .filter(i -> getActiveTeam().get(i).isAlive())
            .findFirst()
            .orElse(-1);

        EntityAvatar replacement = (replaceIndex != -1) ? getActiveTeam().get(replaceIndex) : null;

        if (replacement == null) {
            // No more living team members...
            getPlayer().sendPacket(new PacketWorldPlayerDieNotify(dieType, killedBy));
            // Invoke player team death event.
            PlayerTeamDeathEvent event = new PlayerTeamDeathEvent(getPlayer(),
                getActiveTeam().get(getCurrentCharacterIndex()));
            event.call();
            return;
        }
        // Set index and spawn replacement member
        setCurrentCharacterIndex(replaceIndex);
        getPlayer().getScene().addEntity(replacement);
    }

    public boolean reviveAvatar(Avatar avatar) {
        val entityAvatar = getActiveTeam().stream()
            .filter(entity -> entity.getAvatar() == avatar && !entity.isAlive()).findFirst();

        if (entityAvatar.isPresent()) {
            EntityAvatar entity = entityAvatar.get();
            FightProperty curHpProp = FightProperty.FIGHT_PROP_CUR_HP;
            entity.setFightProperty(curHpProp, 1f);
            getPlayer().sendPacket(new PacketAvatarFightPropUpdateNotify(entity.getAvatar(), curHpProp));
            getPlayer().sendPacket(new PacketAvatarLifeStateChangeNotify(entity.getAvatar()));
            return true;
        }

        return false;
    }

    public boolean healAvatar(Avatar avatar, int healRate, int healAmount) {
        val entityAvatar = getActiveTeam().stream()
            .filter(entity -> entity.getAvatar() == avatar && entity.isAlive()).findFirst();

        if (entityAvatar.isPresent()) {
            EntityAvatar entity = entityAvatar.get();
            FightProperty curHpProp = FightProperty.FIGHT_PROP_CUR_HP;
            float currentHp = entity.getFightProperty(curHpProp);
            float maxHp = entity.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP);
            float finalHp = (float) Math.min((
                currentHp + maxHp * (float) healRate / 100.0 + (float) healAmount / 100.0),
                maxHp);

            entity.setFightProperty(curHpProp, finalHp);
            getPlayer().sendPacket(new PacketAvatarFightPropUpdateNotify(entity.getAvatar(), curHpProp));
            getPlayer().sendPacket(new PacketAvatarLifeStateChangeNotify(entity.getAvatar()));
            return true;
        }

        return false;
    }

    public void respawnTeam() {
        // Make sure all team members are dead
        // Drowning needs revive when there may be other team members still alive.
        //  for (EntityAvatar entity : getActiveTeam()) {
        //      if (entity.isAlive()) {
        //		     return;
        //		}
        //	}
        getPlayer().getStaminaManager().stopSustainedStaminaHandler(); // prevent drowning immediately after respawn

        // Revive all team members
        FightProperty curHpProp = FightProperty.FIGHT_PROP_CUR_HP;
        getActiveTeam().forEach(entity -> {
            float maxHp = entity.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP);
            entity.setFightProperty(curHpProp,maxHp * .4f);
            getPlayer().sendPacket(new PacketAvatarFightPropUpdateNotify(entity.getAvatar(), curHpProp));
            getPlayer().sendPacket(new PacketAvatarLifeStateChangeNotify(entity.getAvatar()));
        });

        // Teleport player and set player position
        Position respawnPos = getRespawnPosition();
        getPlayer().sendPacket(new PacketPlayerEnterSceneNotify(
            getPlayer(),
            EnterType.ENTER_SELF,
            EnterReason.Revival,
            respawnPos == GameConstants.START_POSITION ? 3 : player.getSceneId(),
            respawnPos));
        getPlayer().getPosition().set(respawnPos);
    }

    public Position getRespawnPosition() {
        int sceneId = getPlayer().getSceneId();

        // Get the closest trans point to where the player died.
        return getPlayer().getUnlockedScenePoints(sceneId).stream()
            .map(pointId -> GameData.getScenePointEntryById(sceneId, pointId))
            .filter(point -> point.getPointData().getType().equals("SceneTransPoint"))
            .min(Comparator.comparingDouble(pos ->
                Utils.getDist(pos.getPointData().getTransPosWithFallback(),
                getPlayer().getPosition())))
            .map(scenePointEntry -> scenePointEntry.getPointData().getTransRotWithFallback())
            .orElse(GameConstants.START_POSITION);
    }
    public void saveAvatars() {
        // Save all avatars from active team
        saveAvatars(getActiveTeam());
    }
    public void saveAvatars(@NotNull Collection<EntityAvatar> activeTeam) {
        activeTeam.forEach(entity -> entity.getAvatar().save());
    }

    public void onPlayerLogin() {  // Hack for now to fix resonances on login
        updateTeamResonances();
    }

    public synchronized boolean addNewCustomTeam() {
        // Sanity check - max number of teams.
        if (getTeams().size() == GameConstants.MAX_TEAMS) return false;

        // The id of the new custom team is the lowest id in [5,MAX_TEAMS] that is not yet taken.
        // Create the new team.
        getTeams().put(IntStream.rangeClosed(5, GameConstants.MAX_TEAMS)
            .filter(i -> !getTeams().containsKey(i))
            .findFirst()
            .orElse(-1), new TeamInfo());

        return true;
    }

    public synchronized boolean removeCustomTeam(int id) {
        // Remove team.
        return getTeams().remove(id) != null;
    }
}
