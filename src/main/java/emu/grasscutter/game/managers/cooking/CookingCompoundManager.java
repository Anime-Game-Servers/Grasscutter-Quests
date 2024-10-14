package emu.grasscutter.game.managers.cooking;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.common.ItemParamData;
import emu.grasscutter.data.excels.CompoundData;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.player.BasePlayerManager;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.ActionReason;
import emu.grasscutter.server.packet.send.PacketCompoundDataNotify;
import emu.grasscutter.server.packet.send.PacketGetCompoundDataRsp;
import emu.grasscutter.server.packet.send.PacketPlayerCompoundMaterialRsp;
import emu.grasscutter.server.packet.send.PacketTakeCompoundOutputRsp;
import emu.grasscutter.utils.Utils;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.CompoundQueueData;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.GetCompoundDataReq;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.PlayerCompoundMaterialReq;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.TakeCompoundOutputReq;

import java.util.*;

public class CookingCompoundManager extends BasePlayerManager {
    private static Set<Integer> defaultUnlockedCompounds;
    private static Map<Integer, Set<Integer>> compoundGroups;
    //TODO:bind it to player
    private static Set<Integer> unlocked;

    public CookingCompoundManager(Player player) {
        super(player);
    }

    public static void initialize() {
        defaultUnlockedCompounds = new HashSet<>();
        compoundGroups = new HashMap<>();
        GameData.getCompoundDataMap().forEach((id, compound) -> {
            if (compound.isDefaultUnlocked()) {
                defaultUnlockedCompounds.add(id);
            }
            compoundGroups.computeIfAbsent(compound.getGroupId(), gid -> new HashSet<>()).add(id);
        });
        //TODO:Because we haven't implemented fishing feature,unlock all compounds related to fish.Besides,it should be bound to player rather than manager.
        unlocked = new HashSet<>(defaultUnlockedCompounds);
        if (compoundGroups.containsKey(3))  // Avoid NPE from Resources error
            unlocked.addAll(compoundGroups.get(3));
    }

    private synchronized List<CompoundQueueData> getCompoundQueueData() {
        List<CompoundQueueData> compoundQueueData = new ArrayList<>(player.getActiveCookCompounds().size());
        int currentTime = Utils.getCurrentSeconds();
        for (var item : player.getActiveCookCompounds().values()) {
            var data = new CompoundQueueData();
            data.setCompoundId(item.getCompoundId());
            data.setOutputCount(item.getOutputCount(currentTime));
            data.setOutputTime(item.getOutputTime(currentTime));
            data.setWaitCount(item.getWaitCount(currentTime));
            compoundQueueData.add(data);
        }
        return compoundQueueData;
    }

    public synchronized void handleGetCompoundDataReq(GetCompoundDataReq req) {
        player.sendPacket(new PacketGetCompoundDataRsp(unlocked, getCompoundQueueData()));
    }

    public synchronized void handlePlayerCompoundMaterialReq(PlayerCompoundMaterialReq req) {
        int id = req.getCompoundId(), count = req.getCount();
        CompoundData compound = GameData.getCompoundDataMap().get(id);
        var activeCompounds = player.getActiveCookCompounds();

        //check whether the compound is available
        //TODO:add other compounds,see my pr for detail
        if (!unlocked.contains(id)) {
            player.sendPacket(new PacketPlayerCompoundMaterialRsp(Retcode.RET_FAIL));
            return;
        }
        //check whether the queue is full
        if (activeCompounds.containsKey(id) && activeCompounds.get(id).getTotalCount() + count > compound.getQueueSize()) {
            player.sendPacket(new PacketPlayerCompoundMaterialRsp(Retcode.RET_COMPOUND_QUEUE_FULL));
            return;
        }
        //try to consume raw materials
        if (!player.getInventory().payItems(compound.getInputVec(), count)) {
            //TODO:I'm not sure whether retcode is correct.
            player.sendPacket(new PacketPlayerCompoundMaterialRsp(Retcode.RET_ITEM_COUNT_NOT_ENOUGH));
            return;
        }
        ActiveCookCompoundData c;
        int currentTime = Utils.getCurrentSeconds();
        if (activeCompounds.containsKey(id)) {
            c = activeCompounds.get(id);
            c.addCompound(count, currentTime);
        } else {
            c = new ActiveCookCompoundData(id, compound.getCostTime(), count, currentTime);
            activeCompounds.put(id, c);
        }
        var data = new CompoundQueueData();
        data.setCompoundId(id);
        data.setOutputCount(c.getOutputCount(currentTime));
        data.setOutputTime(c.getOutputTime(currentTime));
        data.setWaitCount(c.getWaitCount(currentTime));
        player.sendPacket(new PacketPlayerCompoundMaterialRsp(data));
    }

    public synchronized void handleTakeCompoundOutputReq(TakeCompoundOutputReq req) {
        //Client won't set compound_id and will set group_id instead.
        int groupId = req.getCompoundGroupId();
        var activeCompounds = player.getActiveCookCompounds();
        int now = Utils.getCurrentSeconds();
        //check available queues
        boolean success = false;
        Map<Integer, GameItem> allRewards = new HashMap<>();
        for (int id : compoundGroups.get(groupId)) {
            if (!activeCompounds.containsKey(id)) continue;
            int quantity = activeCompounds.get(id).takeCompound(now);
            if (activeCompounds.get(id).getTotalCount() == 0) activeCompounds.remove(id);
            if (quantity == 0) continue;
            List<ItemParamData> rewards = GameData.getCompoundDataMap().get(id).getOutputVec();
            for (var i : rewards) {
                if (i.getId() == 0) continue;
                if (allRewards.containsKey(i.getId())) {
                    GameItem item = allRewards.get(i.getId());
                    item.setCount(item.getCount() + i.getCount() * quantity);
                } else {
                    allRewards.put(i.getId(), new GameItem(i.getId(), i.getCount()*quantity));
                }
            }
            success = true;
        }
        //give player the rewards
        if (success) {
            player.getInventory().addItems(allRewards.values(), ActionReason.Compound);
            player.sendPacket(new PacketTakeCompoundOutputRsp(allRewards.values().stream().toList(), Retcode.RET_SUCC));
        } else {
            player.sendPacket(new PacketTakeCompoundOutputRsp(new ArrayList<GameItem>(), Retcode.RET_COMPOUND_NOT_FINISH));
        }
    }

    public void onPlayerLogin() {
        player.sendPacket(new PacketCompoundDataNotify(unlocked,getCompoundQueueData()));
    }
}
