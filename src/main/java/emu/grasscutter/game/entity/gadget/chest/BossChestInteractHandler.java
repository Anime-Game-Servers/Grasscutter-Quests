package emu.grasscutter.game.entity.gadget.chest;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.common.ItemParamData;
import emu.grasscutter.game.entity.gadget.GadgetChest;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.ActionReason;
import emu.grasscutter.server.packet.send.PacketGadgetAutoPickDropInfoNotify;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BossChestInteractHandler implements ChestInteractHandler{
    @Override
    public boolean isTwoStep() {
        return true;
    }

    @Override
    public boolean onInteract(GadgetChest chest, Player player) {
        return this.onInteract(chest,player,false);
    }

    public boolean onInteract(GadgetChest chest, Player player, boolean useCondensedResin) {
        val blossomRewards = player.getScene().getWorld().getOwner().getBlossomManager().onReward(player, chest.getGadget(), useCondensedResin);
        if (blossomRewards) return true;

        val worldDataManager = chest.getGadget().getScene().getWorld().getServer().getWorldDataSystem();
        val chestMetaGadget = chest.getGadget().getMetaGadget();
        val group = chestMetaGadget.getSceneMeta().getGroup(chestMetaGadget.getGroupId());
        if(group == null){
            Grasscutter.getLogger().warn("group is null {} unable to get cfg id {}",
                chestMetaGadget.getGroupId(), chestMetaGadget.getBossChest().getMonsterConfigId());
            return false;
        }
        val monsterCfgId = chestMetaGadget.getBossChest().getMonsterConfigId();
        val groupMonsters = group.getMonsters();
        if(groupMonsters == null){
            Grasscutter.getLogger().warn("group monsters are null {} unable to get cfg id {}",
                chestMetaGadget.getGroupId(), monsterCfgId);
            return false;
        }
        val monster = groupMonsters.get(monsterCfgId);

        val reward = worldDataManager.getRewardByBossId(monster.getMonsterId());

        if (reward == null) {
            val dungeonManager = player.getScene().getDungeonManager();

            if(dungeonManager != null){
                return dungeonManager.getStatueDrops(player, useCondensedResin, chest.getGadget().getGroupId());
            }
            Grasscutter.getLogger().warn("Could not found the reward of boss monster {}", monster.getMonsterId());
            return false;
        }

        val rewards = Arrays.stream(reward.getPreviewItems())
            .map(param -> new GameItem(param.getId(), Math.max(param.getCount(), 1)))
            .toList();

        player.getInventory().addItems(rewards, ActionReason.OpenWorldBossChest);
        player.sendPacket(new PacketGadgetAutoPickDropInfoNotify(rewards));
        return true;
    }
}
