package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.activity.musicgame.MusicGameBeatmap;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetUgcBriefInfoRsp;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.GetUgcBriefInfoReq;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.UgcType;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class HandlerGetUgcBriefInfoReq extends TypedPacketHandler<GetUgcBriefInfoReq> {

	@Override
	public void handle(GameSession session, byte[] header, GetUgcBriefInfoReq req) throws Exception {
        if(req.getUgcType() == UgcType.UGC_TYPE_MUSIC_GAME){
            var musicGameBeatmap = MusicGameBeatmap.getByShareId(req.getUgcGuid());

            if(musicGameBeatmap != null){
                session.send(new PacketGetUgcBriefInfoRsp(musicGameBeatmap.toBriefProto(), req.getUgcType()));
                return;
            }
        }

        session.send(new PacketGetUgcBriefInfoRsp(Retcode.RET_UGC_BRIEF_NOT_FOUND, req.getUgcType()));
	}

}
