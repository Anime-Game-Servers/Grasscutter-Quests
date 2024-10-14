package emu.grasscutter.game.activity.musicgame;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.MusicGameBasicData;
import emu.grasscutter.database.DatabaseHelper;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.music_game.UgcMusicBriefInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(builderMethodName = "of")
public class MusicGamePlayerData {
    Map<Integer, MusicGameRecord> musicGameRecord;
    Map<Long, CustomBeatmapRecord> personalCustomBeatmapRecord;
    Map<Long, CustomBeatmapRecord> othersCustomBeatmapRecord;

    public static MusicGamePlayerData create() {
        return MusicGamePlayerData.of()
            .musicGameRecord(GameData.getMusicGameBasicDataMap().values().stream()
                .collect(Collectors.toMap(MusicGameBasicData::getId, MusicGamePlayerData.MusicGameRecord::create)))
            .personalCustomBeatmapRecord(new HashMap<>())
            .othersCustomBeatmapRecord(new HashMap<>())
            .build();
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder(builderMethodName = "of")
    public static class MusicGameRecord {
        int musicId;
        int maxCombo;
        int maxScore;

        public static MusicGameRecord create(MusicGameBasicData musicGameBasicData) {
            return MusicGameRecord.of()
                .musicId(musicGameBasicData.getId())
                .build();
        }

        public org.anime_game_servers.multi_proto.gi.messages.activity.music_game.MusicGameRecord toProto() {
            return new org.anime_game_servers.multi_proto.gi.messages.activity.music_game.MusicGameRecord(
                true,
                maxCombo,
                maxScore
            );
        }
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder(builderMethodName = "of")
    public static class CustomBeatmapRecord {
        long musicShareId;
        int score;
        boolean settle;

        public UgcMusicBriefInfo toPersonalBriefProto() {
            var musicGameBeatmap = MusicGameBeatmap.getByShareId(musicShareId);
            if(musicGameBeatmap == null){
                return null;
            }

            val player = DatabaseHelper.getPlayerByUid(musicGameBeatmap.getAuthorUid());
            val nickname = player!=null ? player.getNickname() : "UNKNOWN";
            val proto = new UgcMusicBriefInfo();
            proto.setPublished(true);
            proto.setSaveTime(musicGameBeatmap.getCreateTime());
            proto.setMusicId(musicGameBeatmap.getMusicId());
            proto.setMaxScore(musicGameBeatmap.getMaxScore());
            proto.setSaveIdx(musicGameBeatmap.getSavePosition());
            proto.setSavePageType(musicGameBeatmap.getSavePageType());
            proto.setVersion(musicGameBeatmap.getVersion());
            proto.setAfterNoteList(musicGameBeatmap.getAfterNoteList());
            proto.setBeforeNoteList(musicGameBeatmap.getBeforeNoteList());
            proto.setTimeLineEditTime(musicGameBeatmap.getTimeLineEditTime());
            proto.setPublishTime(musicGameBeatmap.getPublishTime());
            proto.setRealTimeEditTime(musicGameBeatmap.getRealTimeEditTime());
            proto.setNoteCount(musicGameBeatmap.getMusicNoteCount());
            proto.setUgcGuid(musicGameBeatmap.getMusicShareId());
            proto.setCreatorNickname(nickname);
            return proto;
        }

        public UgcMusicBriefInfo toOthersBriefProto() {
            var musicGameBeatmap = MusicGameBeatmap.getByShareId(musicShareId);

            val proto = musicGameBeatmap.toBriefProto();
            proto.setSelfMaxScore(score);
            proto.setPlayed(settle);
            return proto;
        }

    }
}


