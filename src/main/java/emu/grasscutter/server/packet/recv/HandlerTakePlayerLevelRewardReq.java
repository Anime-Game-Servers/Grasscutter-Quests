package emu.grasscutter.server.packet.recv;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.common.ItemParamData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.ActionReason;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketTakePlayerLevelRewardRsp;
import org.anime_game_servers.multi_proto.gi.messages.player.TakePlayerLevelRewardReq;

import java.util.List;
import java.util.Set;

public class HandlerTakePlayerLevelRewardReq extends TypedPacketHandler<TakePlayerLevelRewardReq> {
    @Override
    public void handle(GameSession session, byte[] header, TakePlayerLevelRewardReq req) throws Exception {
        Player pl = session.getPlayer();
        synchronized (pl) {
            int level = req.getLevel();
            Set<Integer> rewardedLevels = session.getPlayer().getRewardedLevels();
            if (!rewardedLevels.contains(level)) {// No duplicated reward
                int rewardId = GameData.getPlayerLevelDataMap().get(level).getRewardId();
                if (rewardId != 0) {
                    List<ItemParamData> rewardItems = GameData.getRewardDataMap().get(rewardId).getRewardItemList();
                    pl.getInventory().addItemParamDatas(rewardItems, ActionReason.PlayerUpgradeReward);
                    rewardedLevels.add(level);
                    pl.setRewardedLevels(rewardedLevels);
                    pl.save();
                    session.send(new PacketTakePlayerLevelRewardRsp(level, rewardId));
                }
            }
        }
    }
}
