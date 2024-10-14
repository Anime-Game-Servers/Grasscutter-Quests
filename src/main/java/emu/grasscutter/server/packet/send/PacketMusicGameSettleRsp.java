package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.music_game.MusicGameSettleReq;
import org.anime_game_servers.multi_proto.gi.messages.activity.music_game.MusicGameSettleRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketMusicGameSettleRsp extends BaseTypedPacket<MusicGameSettleRsp> {

    public PacketMusicGameSettleRsp(int musicBasicId, long musicShareId, boolean isNewRecord) {
        super(new MusicGameSettleRsp());

        proto.setMusicBasicId(musicBasicId);
        proto.setUgcGuid(musicShareId);
        proto.setNewRecord(isNewRecord);
    }

    public PacketMusicGameSettleRsp(Retcode errorCode, MusicGameSettleReq req) {
        super(new MusicGameSettleRsp());

        proto.setMusicBasicId(req.getMusicBasicId());
        proto.setRetcode(errorCode);
        proto.setUgcGuid(req.getUgcGuid());
    }
}
