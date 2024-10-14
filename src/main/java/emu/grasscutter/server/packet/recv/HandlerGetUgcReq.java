package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.activity.musicgame.MusicGameBeatmap;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetUgcRsp;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.GetUgcReq;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class HandlerGetUgcReq extends TypedPacketHandler<GetUgcReq> {

    @Override
    public void handle(GameSession session, byte[] header, GetUgcReq req) throws Exception {
        val rsp = switch (req.getUgcType()) {
            case UGC_TYPE_MUSIC_GAME -> handleUgcMusic(session, req);
            default -> new PacketGetUgcRsp(
                Retcode.RET_UGC_DISABLED,
                req
            );
        };

        session.send(rsp);
    }

    private PacketGetUgcRsp handleUgcMusic(GameSession session, GetUgcReq req) {
        val musicGameBeatmap = MusicGameBeatmap.getByShareId(req.getUgcGuid());

        if (musicGameBeatmap == null) {
            return new PacketGetUgcRsp(Retcode.RET_UGC_DATA_NOT_FOUND, req);
        }

        return new PacketGetUgcRsp(musicGameBeatmap.toBriefProto(), musicGameBeatmap.toProto(), req);
    }
}
