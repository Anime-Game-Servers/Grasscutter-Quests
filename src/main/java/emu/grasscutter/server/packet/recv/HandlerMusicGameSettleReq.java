package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.activity.musicgame.MusicGameActivityHandler;
import emu.grasscutter.game.activity.musicgame.MusicGamePlayerData;
import emu.grasscutter.game.props.ActivityType;
import emu.grasscutter.game.props.WatcherTriggerType;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketActivityInfoNotify;
import emu.grasscutter.server.packet.send.PacketMusicGameSettleRsp;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.activity.music_game.MusicGameSettleReq;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class HandlerMusicGameSettleReq extends TypedPacketHandler<MusicGameSettleReq> {

    @Override
    public void handle(GameSession session, byte[] header, MusicGameSettleReq req) throws Exception {
        val activityManager = session.getPlayer().getActivityManager();

        val playerDataOpt = activityManager.getPlayerActivityDataByActivityType(ActivityType.NEW_ACTIVITY_MUSIC_GAME);
        if (playerDataOpt.isEmpty()) {
            session.send(new PacketMusicGameSettleRsp(Retcode.RET_MUSIC_GAME_LEVEL_CONFIG_NOT_FOUND, req));
            return;
        }

        val playerData = playerDataOpt.get();
        val handler = (MusicGameActivityHandler) playerData.getActivityHandler();
        boolean isNewRecord = false;

        // check if custom beatmap
        if (req.getUgcGuid() == 0) {
            session.getPlayer().getActivityManager().triggerWatcher(
                WatcherTriggerType.TRIGGER_FLEUR_FAIR_MUSIC_GAME_REACH_SCORE,
                String.valueOf(req.getMusicBasicId()),
                String.valueOf(req.getScore())
            );

            isNewRecord = handler.setMusicGameRecord(playerData,
                MusicGamePlayerData.MusicGameRecord.of()
                    .musicId(req.getMusicBasicId())
                    .maxCombo(req.getMaxCombo())
                    .maxScore(req.getScore())
                    .build());

            // update activity info
            session.send(new PacketActivityInfoNotify(handler.toProto(playerData, activityManager.getConditionExecutor())));
        } else {
            handler.setMusicGameCustomBeatmapRecord(playerData,
                MusicGamePlayerData.CustomBeatmapRecord.of()
                    .musicShareId(req.getUgcGuid())
                    .score(req.getMaxCombo())
                    .settle(req.isSaveScore())
                    .build());
        }


        session.send(new PacketMusicGameSettleRsp(req.getMusicBasicId(), req.getUgcGuid(), isNewRecord));
    }

}
