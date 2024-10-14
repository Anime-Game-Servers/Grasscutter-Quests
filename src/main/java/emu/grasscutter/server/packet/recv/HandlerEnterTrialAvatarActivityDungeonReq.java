package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.activity.trialavatar.TrialAvatarActivityHandler;
import emu.grasscutter.game.props.ActivityType;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.packet.send.PacketEnterTrialAvatarActivityDungeonRsp;
import org.anime_game_servers.multi_proto.gi.messages.activity.trial.EnterTrialAvatarActivityDungeonReq;
import emu.grasscutter.server.game.GameSession;
import lombok.val;

public class HandlerEnterTrialAvatarActivityDungeonReq extends TypedPacketHandler<EnterTrialAvatarActivityDungeonReq> {
	@Override
    public void handle(GameSession session, byte[] header, EnterTrialAvatarActivityDungeonReq req) throws Exception {
        val handler = session.getPlayer().getActivityManager()
            .getActivityHandlerAs(ActivityType.NEW_ACTIVITY_TRIAL_AVATAR, TrialAvatarActivityHandler.class);

        boolean result = handler.isPresent() && handler.get().enterTrialDungeon(session.getPlayer(), req.getTrialAvatarIndexId(), req.getEnterPointId());

        session.getPlayer().sendPacket(new PacketEnterTrialAvatarActivityDungeonRsp(
            req.getActivityId(),
            req.getTrialAvatarIndexId(),
            result));
	}
}
