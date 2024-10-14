package emu.grasscutter.game.activity.musicgame;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import emu.grasscutter.database.DatabaseHelper;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.music_game.UgcMusicBriefInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.music_game.UgcMusicNote;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.music_game.UgcMusicRecord;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.music_game.UgcMusicTrack;

import java.util.List;
import java.util.Random;

@Entity("music_game_beatmaps")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(builderMethodName = "of")
public class MusicGameBeatmap {

    @Id
    long musicShareId;
    int authorUid;
    int musicId;
    int musicNoteCount;
    int savePosition;
    int savePageType;
    int version;
    List<Integer> afterNoteList;
    List<Integer> beforeNoteList;
    int timeLineEditTime;
    int realTimeEditTime;
    int publishTime;
    int maxScore;
    int createTime;

    List<List<BeatmapNote>> beatmap;

    public static MusicGameBeatmap getByShareId(long musicShareId){
        return DatabaseHelper.getMusicGameBeatmap(musicShareId);
    }

    public void save(){
        if(musicShareId == 0){
            musicShareId = new Random().nextLong(100000000000000L,999999999999999L);
        }
        DatabaseHelper.saveMusicGameBeatmap(this);
    }

    public static List<List<BeatmapNote>> parse(List<UgcMusicTrack> beatmapItemListList) {
        return beatmapItemListList.stream()
            .map(item -> item.getMusicNoteList().stream()
                .map(BeatmapNote::parse)
                .toList())
            .toList();
    }

    public UgcMusicRecord toProto(){
        return new UgcMusicRecord(musicId,
            beatmap.stream()
                .map(this::musicBeatmapListToProto)
                .toList()
            );
    }

    public UgcMusicBriefInfo toBriefProto(){
        var player = DatabaseHelper.getPlayerByUid(authorUid);

        val proto = new UgcMusicBriefInfo();
        proto.setMusicId(musicId);
        proto.setNoteCount(musicNoteCount);
        proto.setUgcGuid(musicShareId);
        proto.setMaxScore(maxScore);
        proto.setPublishTime(createTime);
        proto.setCreatorNickname(player.getNickname());
        proto.setSavePageType(savePageType);
        proto.setVersion(version);
        proto.setAfterNoteList(afterNoteList);
        proto.setBeforeNoteList(beforeNoteList);
        proto.setTimeLineEditTime(timeLineEditTime);
        proto.setPublishTime(publishTime);
        proto.setRealTimeEditTime(realTimeEditTime);
        return proto;
    }

    private UgcMusicTrack musicBeatmapListToProto(List<BeatmapNote> beatmapNoteList) {
        return new UgcMusicTrack(beatmapNoteList.stream()
            .map(BeatmapNote::toProto)
            .toList());
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder(builderMethodName = "of")
    @Entity
    public static class BeatmapNote{
        int startTime;
        int endTime;

        public static BeatmapNote parse(UgcMusicNote note){
            return BeatmapNote.of()
                .startTime(note.getStartTime())
                .endTime(note.getEndTime())
                .build();
        }

        public UgcMusicNote toProto(){
            return new UgcMusicNote(startTime, endTime);
        }
    }
}
