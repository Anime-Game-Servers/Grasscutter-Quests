package emu.grasscutter.game.managers;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.game.city.CityInfoData;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.player.BasePlayerManager;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.ActionReason;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.game.quest.enums.QuestContent;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.ChangeHpReason;
import org.anime_game_servers.multi_proto.gi.messages.general.PropChangeReason;
import emu.grasscutter.server.packet.send.PacketEntityFightPropChangeReasonNotify;
import emu.grasscutter.server.packet.send.PacketEntityFightPropUpdateNotify;
import emu.grasscutter.server.packet.send.PacketSceneForceUnlockNotify;
import lombok.val;
import emu.grasscutter.server.packet.send.PacketLevelupCityRsp;

import org.anime_game_servers.game_data_models.data.city.CityData;
import org.anime_game_servers.game_data_models.data.city.CityLevelUpData;
import org.anime_game_servers.gi_lua.models.ScriptArgs;
import org.anime_game_servers.gi_lua.models.constants.EventType;
import org.slf4j.Logger;

import java.util.*;

/*
 * Manages the Statue of the Seven and CityLevel
 */
public class SotSManager extends BasePlayerManager {

    // NOTE: Spring volume balance *1  = fight prop HP *100

    private final Logger logger = Grasscutter.getLogger();
    private Timer autoRecoverTimer;
    private final boolean enablePriorityHealing = false;

    public final static int GlobalMaximumSpringVolume = PlayerProperty.PROP_MAX_SPRING_VOLUME.getMax();

    public SotSManager(Player player) {
        super(player);
    }

    public boolean getIsAutoRecoveryEnabled() {
        return player.getProperty(PlayerProperty.PROP_IS_SPRING_AUTO_USE) == 1;
    }

    public void setIsAutoRecoveryEnabled(boolean enabled) {
        player.setProperty(PlayerProperty.PROP_IS_SPRING_AUTO_USE, enabled ? 1 : 0);
        player.save();
    }

    public int getAutoRecoveryPercentage() {
        return player.getProperty(PlayerProperty.PROP_SPRING_AUTO_USE_PERCENT);
    }

    public void setAutoRecoveryPercentage(int percentage) {
        player.setProperty(PlayerProperty.PROP_SPRING_AUTO_USE_PERCENT, percentage);
        player.save();
    }

    public long getLastUsed() {
        return player.getSpringLastUsed();
    }

    public void setLastUsed() {
        player.setSpringLastUsed(System.currentTimeMillis() / 1000);
        player.save();
    }

    public int getMaxVolume() {
        return player.getProperty(PlayerProperty.PROP_MAX_SPRING_VOLUME);
    }

    public void setMaxVolume(int volume) {
        player.setProperty(PlayerProperty.PROP_MAX_SPRING_VOLUME, volume);
        player.save();
    }

    public int getCurrentVolume() {
        return player.getProperty(PlayerProperty.PROP_CUR_SPRING_VOLUME);
    }

    public void setCurrentVolume(int volume) {
        player.setProperty(PlayerProperty.PROP_CUR_SPRING_VOLUME, volume);
        setLastUsed();
        player.save();
    }

    public void handleEnterTransPointRegionNotify() {
        logger.trace("Player entered statue region");
        autoRevive();
        if (autoRecoverTimer == null) {
            autoRecoverTimer = new Timer();
            autoRecoverTimer.schedule(new AutoRecoverTimerTick(), 2500, 15000);
        }
    }

    public void handleExitTransPointRegionNotify() {
        logger.trace("Player left statue region");
        if (autoRecoverTimer != null) {
            autoRecoverTimer.cancel();
            autoRecoverTimer = null;
        }
    }

    // autoRevive automatically revives all team members.
    public void autoRevive() {
        player.getTeamManager().getActiveTeam().forEach(entity -> {
            boolean isAlive = entity.isAlive();
            if (isAlive) {
                return;
            }
            logger.trace("Reviving avatar " + entity.getAvatar().getAvatarData().getName());
            player.getTeamManager().reviveAvatar(entity.getAvatar());
            player.getTeamManager().healAvatar(entity.getAvatar(), 30, 0);
        });
    }

    private class AutoRecoverTimerTick extends TimerTask {
        // autoRecover checks player setting to see if auto recover is enabled, and refill HP to the predefined level.
        public void run() {
            refillSpringVolume();

            logger.trace("isAutoRecoveryEnabled: " + getIsAutoRecoveryEnabled() + "\tautoRecoverPercentage: " + getAutoRecoveryPercentage());

            if (getIsAutoRecoveryEnabled()) {
                List<EntityAvatar> activeTeam = player.getTeamManager().getActiveTeam();
                // When the statue does not have enough remaining volume:
                //      Enhanced experience: Enable priority healing
                //                              The current active character will get healed first, then sequential.
                //      Vanilla experience: Disable priority healing
                //                              Sequential healing based on character index.
                int priorityIndex = enablePriorityHealing ? player.getTeamManager().getCurrentCharacterIndex() : -1;
                if (priorityIndex >= 0) {
                    checkAndHealAvatar(activeTeam.get(priorityIndex));
                }
                for (int i = 0; i < activeTeam.size(); i++) {
                    if (i != priorityIndex) {
                        checkAndHealAvatar(activeTeam.get(i));
                    }
                }
            }
        }
    }

    public void checkAndHealAvatar(EntityAvatar entity) {
        int maxHP = (int) (entity.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP) * 100);
        int currentHP = (int) (entity.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP) * 100);
        if (currentHP == maxHP) {
            return;
        }
        int targetHP = maxHP * getAutoRecoveryPercentage() / 100;

        if (targetHP > currentHP) {
            int needHP = targetHP - currentHP;
            int currentVolume = getCurrentVolume();
            if (currentVolume >= needHP) {
                // sufficient
                setCurrentVolume(currentVolume - needHP);
            } else {
                // insufficient balance
                needHP = currentVolume;
                setCurrentVolume(0);
            }
            if (needHP > 0) {
                logger.trace("Healing avatar " + entity.getAvatar().getAvatarData().getName() + " +" + needHP);
                player.getTeamManager().healAvatar(entity.getAvatar(), 0, needHP);
                player.getSession().send(new PacketEntityFightPropChangeReasonNotify(entity, FightProperty.FIGHT_PROP_CUR_HP,
                    ((float) needHP / 100), List.of(3), PropChangeReason.PROP_CHANGE_STATUE_RECOVER,
                    ChangeHpReason.CHANGE_HP_ADD_STATUE));
                player.getSession().send(new PacketEntityFightPropUpdateNotify(entity, FightProperty.FIGHT_PROP_CUR_HP));

            }
        }
    }

    public void refillSpringVolume() {
        // Temporary: Max spring volume depends on level of the statues in Mondstadt and Liyue. Override until we have statue level.
        // TODO: remove
        // https://genshin-impact.fandom.com/wiki/Statue_of_The_Seven#:~:text=region%20of%20Inazuma.-,Statue%20Levels,-Upon%20first%20unlocking
        setMaxVolume(8500000);
        // Temporary: Auto enable 100% statue recovery until we can adjust statue settings in game
        // TODO: remove
        setAutoRecoveryPercentage(100);
        setIsAutoRecoveryEnabled(true);

        int maxVolume = getMaxVolume();
        int currentVolume = getCurrentVolume();
        if (currentVolume < maxVolume) {
            long now = System.currentTimeMillis() / 1000;
            int secondsSinceLastUsed = (int) (now - getLastUsed());
            // 15s = 1% max volume
            int volumeRefilled = secondsSinceLastUsed * maxVolume / 15 / 100;
            logger.trace("Statue has refilled HP volume: " + volumeRefilled);
            currentVolume = Math.min(currentVolume + volumeRefilled, maxVolume);
            logger.trace("Statue remaining HP volume: " + currentVolume);
            setCurrentVolume(currentVolume);
        }
    }

    // City/Statue level handling

    public CityData getCityByAreaId(int areaId) {
        return GameData.getCityDataMap().values().stream()
                .filter(city -> city.getAreaIds().contains(areaId))
                .findFirst()
                .orElse(null);
    }

    public CityInfoData getCityInfo(int cityId) {
        if (player.getCityInfoData() == null) player.setCityInfoData(new HashMap<>());
        var cityInfo = player.getCityInfoData().get(cityId);
        if (cityInfo == null) {
            cityInfo = new CityInfoData(cityId);
            player.getCityInfoData().put(cityId, cityInfo);
        }
        return cityInfo;
    }

    public void addCityInfo(CityInfoData cityInfoData) {
        if (player.getCityInfoData() == null) player.setCityInfoData(new HashMap<>());

        player.getCityInfoData().put(cityInfoData.getCityId(), cityInfoData);
    }

    public void levelUpSotS(int areaId, int sceneId, int itemNum) {
        if (itemNum <= 0) return;

        // search city by areaId
        var city = this.getCityByAreaId(areaId);
        if (city == null) return; // TODO return error to allow sending packet with error
        var cityId = city.getCityId();

        // check data level up
        var cityInfo = this.getCityInfo(cityId);

        val completedLevels = new ArrayList<CityLevelUpData>();
        var level = cityInfo.getLevel();
        var prevCrystalProgress = cityInfo.getNumCrystal();
        var itemCountLeft = itemNum;
        var crystalProgress = 0;
        while(level<10 && itemCountLeft > 0){
            val nextLevelUpData = GameData.getCityLevelUpData(cityId, level + 1);
            if(nextLevelUpData == null || nextLevelUpData.getConsumeItem() == null){
                break;
            }

            val cost = nextLevelUpData.getConsumeItem().getCount() - prevCrystalProgress;

            // TODO properly include already added oculi for the cost calculations
            // player might not want to level up all he can
            if(cost > itemCountLeft){
                // pay the rest the payer wanted to offer
                if(getPlayer().getInventory().payItem(nextLevelUpData.getConsumeItem().getItemId(), itemCountLeft)){
                    crystalProgress = itemCountLeft + prevCrystalProgress;
                    itemCountLeft = 0;
                }
                break;
            }


            if(getPlayer().getInventory().payItem(nextLevelUpData.getConsumeItem().getItemId(), cost)){
                completedLevels.add(nextLevelUpData);
                level++;
                itemCountLeft -= cost;
                prevCrystalProgress = 0;
            } else {
                break;
            }
        }

        // update number oculi
        cityInfo.setNumCrystal(crystalProgress);
        cityInfo.setLevel(level);

        completedLevels.forEach(levelData -> {
            // update player properties
            val reward = GameData.getRewardDataMap().get(levelData.getRewardId());
            if(reward != null){
                getPlayer().getInventory().addRewardData(reward, ActionReason.CityLevelupReward);
            }
            if(levelData.getActions()!=null) {
                handleLevelUpActions(levelData.getActions());
            }
        });

        // update data
        this.addCityInfo(cityInfo);


        handleLevelUpEvents(cityId, level);

        // Packets
        player.sendPacket(
                new PacketLevelupCityRsp(
                        sceneId, level, cityId, crystalProgress, areaId, Retcode.RET_SUCC));
    }

    private void handleLevelUpActions(List<CityLevelUpData.CityLevelUpAction> actions){
        actions.forEach(action -> {
            if(action.getType() == null) return;
            switch (action.getType()) {
                case WORLD_AREA_ACTION_IMPROVE_STAMINA -> {
                    if (action.getParam1() == null || action.getParam1().isEmpty()) {
                        break;
                    }
                    // update max stamina and notify client
                    getPlayer().setProperty(
                        PlayerProperty.PROP_MAX_STAMINA,
                        getPlayer().getProperty(PlayerProperty.PROP_MAX_STAMINA)
                            + action.getParam1().get(0) * 100,
                        true);
                }
                case WORLD_AREA_ACTION_UNLOCK_FORCE -> {
                    if (action.getParam1() == null || action.getParam1().isEmpty()) {
                        break;
                    }
                    // this might need to be persisted
                    getPlayer().getScene().unlockForce(action.getParam1().get(0));
                }
                case WORLD_AREA_ACTION_ACTIVATE_ITEM -> {
                    // TODO: implement
                }
                default -> {
                }
            }
        });
    }

    private void handleLevelUpEvents(int cityId, int level) {
        player.getQuestManager().queueEvent(QuestContent.QUEST_CONTENT_CITY_LEVEL_UP, cityId, level);
        player.getScene().getScriptManager().callEvent(new ScriptArgs(0, EventType.EVENT_CITY_LEVELUP, cityId, level));
    }
}
