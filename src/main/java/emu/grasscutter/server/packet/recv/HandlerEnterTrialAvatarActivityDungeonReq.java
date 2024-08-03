package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.activity.trialavatar.TrialAvatarActivityHandler;
import org.anime_game_servers.game_data_models.data.activity.ActivityType;
import emu.grasscutter.net.packet.Opcodes;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.packet.PacketHandler;
import emu.grasscutter.server.packet.send.PacketEnterTrialAvatarActivityDungeonRsp;
import emu.grasscutter.net.proto.EnterTrialAvatarActivityDungeonReqOuterClass.EnterTrialAvatarActivityDungeonReq;
import emu.grasscutter.server.game.GameSession;
import lombok.val;

@Opcodes(PacketOpcodes.EnterTrialAvatarActivityDungeonReq)
public class HandlerEnterTrialAvatarActivityDungeonReq extends PacketHandler {

	@Override
	public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        val req = EnterTrialAvatarActivityDungeonReq.parseFrom(payload);

        val handler = session.getPlayer().getActivityManager()
            .getActivityHandlerAs(ActivityType.NEW_ACTIVITY_TRIAL_AVATAR, TrialAvatarActivityHandler.class);

        boolean result = handler.isPresent() && handler.get().enterTrialDungeon(session.getPlayer(), req.getTrialAvatarIndexId(), req.getEnterPointId());

        session.getPlayer().sendPacket(new PacketEnterTrialAvatarActivityDungeonRsp(
            req.getActivityId(),
            req.getTrialAvatarIndexId(),
            result));
	}

}
