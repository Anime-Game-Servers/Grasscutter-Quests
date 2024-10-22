package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.dungeons.DungeonManager;
import emu.grasscutter.game.player.Player.SceneLoadState;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.*;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.scene.SceneForceUnlockNotify;
import org.anime_game_servers.multi_proto.gi.messages.scene.SceneInitFinishReq;

import java.util.Optional;

public class HandlerSceneInitFinishReq extends TypedPacketHandler<SceneInitFinishReq> {

	@Override
	public void handle(GameSession session, byte[] header, SceneInitFinishReq req) throws Exception {
		// Info packets
		session.send(new PacketServerTimeNotify());
        Optional.ofNullable(session.getPlayer().getScene().getDungeonManager())
            .ifPresent(DungeonManager::sendDungeonInfoPacket);

		session.send(new PacketWorldPlayerInfoNotify(session.getPlayer().getWorld()));
		session.send(new PacketWorldDataNotify(session.getPlayer().getWorld()));
        session.send(new PacketWorldOwnerBlossomBriefInfoNotify(session.getPlayer().getWorld()));
		session.send(new PacketPlayerWorldSceneInfoListNotify(session.getPlayer()));
        session.send(new PacketLevelTagDataNotify(session.getPlayer()));
        session.send(new BaseTypedPacket<>(new SceneForceUnlockNotify()) {});
		session.send(new PacketHostPlayerNotify(session.getPlayer().getWorld()));
		session.send(new PacketSceneDataNotify(session.getPlayer()));
		session.send(new PacketSceneTimeNotify(session.getPlayer()));
		session.send(new PacketPlayerGameTimeNotify(session.getPlayer()));
		session.send(new PacketPlayerEnterSceneInfoNotify(session.getPlayer()));

		session.getPlayer().getScene().reloadWeathers();
		session.getPlayer().updateWeather(session.getPlayer().getScene());
		val area = session.getPlayer().getScene().getWeatherAreas().get(session.getPlayer().getWeatherAreaId());
		if(area != null)
			session.send(new PacketSceneAreaWeatherNotify(area.getConfig().getAreaID(), area.getCurrentClimateType(), area.getTransDuration()));
		else //TODO: Remove old climate implementation, you can use areaId 0 and climate type none, but needs testing
			session.send(new PacketSceneAreaWeatherNotify(session.getPlayer()));
		session.send(new PacketScenePlayerInfoNotify(session.getPlayer().getWorld()));
		session.send(new PacketSceneTeamUpdateNotify(session.getPlayer()));

		session.send(new PacketSyncTeamEntityNotify(session.getPlayer()));
		session.send(new PacketSyncScenePlayTeamEntityNotify(session.getPlayer()));

		// Done Packet
		session.send(new PacketSceneInitFinishRsp(session.getPlayer()));

		// Set state
		session.getPlayer().setSceneLoadState(SceneLoadState.INIT);
	}
}
