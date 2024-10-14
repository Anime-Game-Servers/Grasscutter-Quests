package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.activity.trialavatar.TrialAvatarActivityHandler;
import emu.grasscutter.game.props.ActivityType;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketReceivedTrialAvatarActivityRewardRsp;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.activity.trial.ReceivedTrialAvatarActivityRewardReq;

public class HandlerReceivedTrialAvatarActivityRewardReq extends TypedPacketHandler<ReceivedTrialAvatarActivityRewardReq> {
	@Override
    public void handle(GameSession session, byte[] header, ReceivedTrialAvatarActivityRewardReq req) throws Exception {
        val player = session.getPlayer();
        val handler = player.getActivityManager().getActivityHandlerAs(ActivityType.NEW_ACTIVITY_TRIAL_AVATAR, TrialAvatarActivityHandler.class);

        boolean result = handler.isPresent() && handler.get().getReward(player, req.getTrialAvatarIndexId());

        session.getPlayer().sendPacket(new PacketReceivedTrialAvatarActivityRewardRsp(
            5002, // trial activity id
            req.getTrialAvatarIndexId(),
            result));
	}
}
