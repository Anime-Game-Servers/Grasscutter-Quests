package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.GetUgcReq;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.GetUgcRsp;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.music_game.UgcMusicBriefInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.music_game.UgcMusicRecord;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketGetUgcRsp extends BaseTypedPacket<GetUgcRsp> {

    public PacketGetUgcRsp(UgcMusicBriefInfo briefInfo, UgcMusicRecord musicRecord, GetUgcReq req) {
        super(new GetUgcRsp(Retcode.RET_SUCC,
            briefInfo.getUgcGuid(),
            req.getUgcType(),
            req.getUgcRecordUsage(),
            new GetUgcRsp.Record.MusicRecord(musicRecord),
            new GetUgcRsp.Brief.MusicBriefInfo(briefInfo)));
    }
    public PacketGetUgcRsp(Retcode errorCode, GetUgcReq req) {
        super( new GetUgcRsp(errorCode,
            req.getUgcGuid(),
            req.getUgcType(),
            req.getUgcRecordUsage()));
    }
}
